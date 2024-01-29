pipeline {
    agent {
        kubernetes {
            label 'K8S-With-Docker'
        }
    }
    stages {
        stage('Checkout Source Code') {
            steps {
                script {
                    checkout([$class: 'GitSCM',
                              branches: [[name: portfolio_branch]],
                              extensions: [],
                              submoduleCfg: [],
                              userRemoteConfigs: [[url: 'https://github.com/ChayFadida/Portfolio.git']]])
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    commitHash = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    docker.withRegistry(DOCKER_REGISTRY, 'harbor-pull-secret') {
                        def docker_image = docker.build("${DOCKER_REGISTRY}/portfolio/portfolio-front:_${commitHash}", "-f Dockerfile.portfolio .")
                        docker_image.push()
                        sh "docker rmi ${docker_image.id}"
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