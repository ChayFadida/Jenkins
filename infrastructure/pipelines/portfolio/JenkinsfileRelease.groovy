pipeline {
    agent {
        kubernetes {
            label 'K8S-With-Docker'
        }
    }
    environment {
        CHECKED_OUT_BRANCH = ''
        IMAGE_TAG = ''
    }
    stages {
        stage('Checkout Source Code') {
            steps {
                cleanWs()
                dir('portfolio-src') {
                    script {
                        def gitInfo = checkout([$class: 'GitSCM',
                                branches: [[name: portfolio_branch]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/Portfolio.git']]])
                        CHECKED_OUT_BRANCH = gitInfo.GIT_BRANCH.split('/')[-1]
                    }
                }

                dir('portfolio-cd') {
                    script {
                        checkout([$class: 'GitSCM',
                                branches: [[name: portfolio_branch]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/PortfolioGitOps.git']]])
                        sh "git checkout ${CHECKED_OUT_BRANCH}"
                    }
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                dir('portfolio-src') {
                    script {
                        def commitHash = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                        IMAGE_TAG = "${CHECKED_OUT_BRANCH}_${commitHash}"
                        
                        // Retrieve credentials
                        def REACT_APP_EMAILJS_TEMPLATE_ID = credentials('PORTFOLIO_REACT_APP_EMAILJS_TEMPLATE_ID')
                        def REACT_APP_EMAILJS_USER_ID = credentials('PORTFOLIO_REACT_APP_EMAILJS_USER_ID')
                        def REACT_APP_EMAILJS_SERVICE_ID = credentials('PORTFOLIO_REACT_APP_EMAILJS_SERVICE_ID')
                        
                        // Check if credentials are valid
                        if (REACT_APP_EMAILJS_TEMPLATE_ID && REACT_APP_EMAILJS_USER_ID && REACT_APP_EMAILJS_SERVICE_ID) {
                            // Build docker arguments
                            def dockerArgs = "--build-arg REACT_APP_EMAILJS_TEMPLATE_ID=${REACT_APP_EMAILJS_TEMPLATE_ID} --build-arg REACT_APP_EMAILJS_USER_ID=${REACT_APP_EMAILJS_USER_ID} --build-arg=${REACT_APP_EMAILJS_SERVICE_ID}"
                            docker.withRegistry("https://${DOCKER_REGISTRY}", 'harbor-cred-secret') {
                                def docker_image = docker.build("${DOCKER_REGISTRY}/portfolio/portfolio-front:${IMAGE_TAG}", "-f Dockerfile.portfolio ${dockerArgs} .")
                                docker_image.push()
                                sh "docker rmi ${docker_image.id}"
                            }
                        } else {
                            error("Missing credentials")
                        }
                    }
                }
            }
        }
        
        stage('Update Deployment') {
            steps {
                dir('portfolio-cd') {
                    script {            
                        // Define the path to the Deployment YAML file based on the branch/environment
                        def deploymentPath = "environments/${CHECKED_OUT_BRANCH}/deployment.yml"

                        // Read the Deployment YAML file
                        def deploymentYaml = readFile(deploymentPath)

                        // Update the image tag in the Deployment YAML
                        deploymentYaml = deploymentYaml.replaceAll("(?<=image: harbor\\.chay-techs\\.com\\/portfolio\\/portfolio-front:)\\S+", IMAGE_TAG)

                        // Write the modified Deployment YAML back to the file
                        writeFile(file: deploymentPath, text: deploymentYaml)

                        withCredentials([usernamePassword(credentialsId: 'github-secret-login', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh "git config --global user.email ${GIT_MAIL}"
                            sh "git config --global user.name ${GIT_USERNAME}"
                            sh "git add ${deploymentPath}"
                            sh 'git commit -m "Update Docker image tag in deployment.yml"'
                            sh "git push https://$USERNAME:$PASSWORD@github.com/ChayFadida/PortfolioCD.git"
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully. Pushed changes to the ArgoCD Git repository.'
        }
    }
}