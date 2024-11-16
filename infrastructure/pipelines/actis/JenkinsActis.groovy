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
                dir('actis-src') {
                    script {
                        def gitInfo = checkout([$class: 'GitSCM',
                                branches: [[name: actis_branch]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/actis.git']]])
                        CHECKED_OUT_BRANCH = gitInfo.GIT_BRANCH.split('/')[-1]
                    }
                }

                dir('actis-cd') {
                    script {
                        checkout([$class: 'GitSCM',
                                branches: [[name: actis_branch]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/ActisGitOps.git']]])
                        sh "git checkout ${CHECKED_OUT_BRANCH}"
                    }
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                dir('actis-src') {
                    sh "ls"
                    script {
                        def commitHash = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                        IMAGE_TAG = "${CHECKED_OUT_BRANCH}_${commitHash}"
                        
                        // Build docker arguments
                        docker.withRegistry("https://${DOCKER_REGISTRY}", 'harbor-cred-secret') {
                            def docker_image = docker.build("${DOCKER_REGISTRY}/actis/website:${IMAGE_TAG}", "-f Dockerfile .")
                            docker_image.push()
                            sh "docker rmi ${docker_image.id}"
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
                        def deploymentPath = "deployment.yml"

                        // Read the Deployment YAML file
                        def deploymentYaml = readFile(deploymentPath)

                        // Update the image tag in the Deployment YAML
                        deploymentYaml = deploymentYaml.replaceAll("(?<=image: harbor\\.chay-techs\\.com\\/actis\\/website:)\\S+", IMAGE_TAG)

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