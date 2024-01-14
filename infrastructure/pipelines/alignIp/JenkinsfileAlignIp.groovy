pipeline {
    agent any

    stages {
        stage('CheckOut SRC'){
            checkout scmGit(
                branches: [[name: 'master']],
                extensions: [ cloneOption(shallow: true) ],
                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/Utils.git']])
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
