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
        
        stage('Update Deployment') {
            steps {
                dir('portfolio-cd') {
                    script {
                        gitInfo = checkout([$class: 'GitSCM',
                                branches: [[name: portfolio_branch]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/PortfolioCD.git']]])
                    }
                }
            }
        }
    }

    stage('Update Deployment') {
        steps {
            dir('portfolio-cd') {
                script {
                    // Read the Deployment YAML file
                    def deploymentYaml = readFile('deployment.yml')

                    // Update the image tag in the Deployment YAML
                    deploymentYaml = deploymentYaml.replaceAll(/image: harbor.chay-techs.com\/portfolio\/portfolio-front:.*$/, "image: harbor.chay-techs.com/portfolio/portfolio-front:${tag}")

                    // Write the modified Deployment YAML back to the file
                    writeFile(file: 'deployment.yml', text: deploymentYaml)

                    // Commit and push the changes to the ArgoCD Git repository
                    sh 'git add .'
                    sh 'git commit -m "Update Docker image tag in deployment.yml"'
                    sh "git push origin ${CHECKED_OUT_BRANCH}"
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