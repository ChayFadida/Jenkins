pipeline {
    agent {
        kubernetes {
            label 'K8S-With-Docker'
        }
    }
    environment {
        CHECKED_OUT_BRANCH = ''
    }
    stages {
        stage('Checkout Source Code') {
            steps {
                cleanWs()
                dir('portfolio-src') {
                    script {
                        gitInfo = checkout([$class: 'GitSCM',
                                branches: [[name: portfolio_branch]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/Portfolio.git']]])
                        CHECKED_OUT_BRANCH = gitInfo.GIT_BRANCH.split('/')[-1]
                    }
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                dir('portfolio-src') {
                    script {
                        def commitHash = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                        def tag = "${CHECKED_OUT_BRANCH}_${commitHash}"
                        docker.withRegistry("https://${DOCKER_REGISTRY}", 'harbor-pull-secret') {
                            def docker_image = docker.build("${DOCKER_REGISTRY}/portfolio/portfolio-front:${tag}", "-f Dockerfile.portfolio .")
                            docker_image.push()
                            sh "docker rmi ${docker_image.id}"
                        }
                    }
                }
            }
        }
        
        stage('Checkout Deployment Source Code') {
            steps {
                dir('portfolio-cd') {
                    script {
                        gitInfo = checkout([$class: 'GitSCM',
                                branches: [[name: portfolio_branch.split('/')[-1]]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/PortfolioCD.git']]])
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
                        deploymentYaml = deploymentYaml.replaceAll("image: harbor.chay-techs.com\\/portfolio\\/portfolio-front:+([\\w\\.\\-\\/:@]+)", "niceee")

                        // Write the modified Deployment YAML back to the file
                        writeFile(file: deploymentPath, text: deploymentYaml)

                        // Commit and push the changes to the ArgoCD Git repository
                        sh """
                        git config --global user.email "you@example.com"
                            git config --global user.name "Your Name"
                        """
                        sh "git branch"
                        sh "git add ${deploymentPath}"
                        sh 'git commit -m "Update Docker image tag in deployment.yml"'
                        sh "git push"        
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