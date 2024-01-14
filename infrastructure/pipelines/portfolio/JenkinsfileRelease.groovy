pipeline {
    agent {
        kubernetes {
            label 'jenkins-jenkins-agent-harbor'
        }
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/ChayFadida/Portfolio.git'
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    def dockerImage = docker.build("${params.DOCKER_REGISTRY}/${params.APP_NAME}:${params.IMAGE_TAG}")
                    docker.withRegistry("${params.DOCKER_REGISTRY}", 'chay-techs-registry-key') {
                        dockerImage.push()
                    }
                }
            }
        }

        stage('Update Deployment') {
            steps {
                script {
                    sh "kubectl set image deployment/${params.APP_NAME} ${params.APP_NAME}=${params.DOCKER_REGISTRY}/${params.APP_NAME}:${params.IMAGE_TAG} -n ${params.NAMESPACE}"
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully. Deployed new image to Kubernetes.'
        }
    }
}
