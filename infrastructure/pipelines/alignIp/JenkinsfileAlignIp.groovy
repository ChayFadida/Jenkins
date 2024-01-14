pipeline {
    agent any

    stages {
        stage('Build Image') {
            steps {
                script {
                    def imageName = 'your-docker-image-name'
                    def dockerfile = 'Dockerfile' // Replace with your actual Dockerfile name

                    // Build Docker image
                    sh "docker build -t $imageName -f $dockerfile ."
                }
            }
        }

        stage('Run Container') {
            steps {
                script {
                    def containerName = 'your-container-name'
                    
                    // Run Docker container
                    sh "docker run --name $containerName -d $imageName"
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
