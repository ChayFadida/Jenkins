pipeline {
    agent {
        kubernetes {
            // Define the label that matches the pod template in your Kubernetes cloud configuration
            label 'jenkins-jenkins-agent'
        }
    }

    stages {
        stage('CheckOut SRC') {
            steps {
                script {
                    checkout([$class: 'GitSCM', 
                              branches: [[name: 'master']],
                              extensions: [[$class: 'CloneOption', shallow: true]],
                              userRemoteConfigs: [[url: 'https://github.com/ChayFadida/Utils.git']]])
                }
            }
        }
    }

    post {
        always {
            // Cleanup: Stop and remove the Docker container
            script {
                def containerName = 'your-container-name'
                
                sh "docker stop $containerName || true"
                sh "docker rm $containerName || true"
            }
        }
    }
}
