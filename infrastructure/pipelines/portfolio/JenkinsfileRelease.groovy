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

        stage('Update Deployment') {
            steps {
                script {
                    sh "kubectl get pod"
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
