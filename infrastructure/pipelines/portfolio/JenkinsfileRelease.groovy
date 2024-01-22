pipeline {
    agent {
        kubernetes {
            label 'jenkins-jenkins-agent-harbor'
        }
    }
    stages {
        stage('Checkout Source Code') {
            steps {
                script {
                    checkout([$class: 'GitSCM',
                              branches: [[name: 'master']],
                              extensions: [],
                              submoduleCfg: [],
                              userRemoteConfigs: [[url: 'https://github.com/ChayFadida/Portfolio.git']]])
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'REACT_APP_EMAILJS_SERVICE_ID', variable: 'REACT_APP_EMAILJS_SERVICE_ID'),
                                     string(credentialsId: 'REACT_APP_EMAILJS_TEMPLATE_ID', variable: 'REACT_APP_EMAILJS_TEMPLATE_ID'),
                                     string(credentialsId: 'REACT_APP_EMAILJS_USER_ID', variable: 'REACT_APP_EMAILJS_USER_ID')]) {
                        sh "docker build -t harbor.chay-techs.com/portfolio/portfolio-front:testim -f Dockerfile.portfolio ."
                        // sh "docker login -u your_docker_username -p your_docker_password"
                        // sh "docker push harbor.chay-techs.com/portfolio/portfolio-front:testim"
                    }
                }
            }
        }
        // stage('Update Deployment') {
        //     steps {
        //         script {
        //             // Update the Kubernetes manifests or any other necessary files here
        //             sh "sed -i 's/old_value/new_value/g' path/to/your/deployment.yaml"

        //             // Commit and push changes to the Git repository
        //             sh "git config user.email 'jenkins@example.com'"
        //             sh "git config user.name 'Jenkins'"
        //             sh "git add ."
        //             sh "git commit -m 'Update manifests for new image'"
        //             sh "git push origin main"  // Replace 'main' with your branch name
        //         }
        //     }
        // }
    }

    post {
        success {
            echo 'Pipeline completed successfully. Pushed changes to the ArgoCD Git repository.'
        }
    }
}