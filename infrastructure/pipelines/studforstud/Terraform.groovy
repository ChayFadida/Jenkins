pipeline {
    agent {
        kubernetes {
            label 'K8S-With-Docker'
        }
    }
    stages {
        stage('Checkout Source Code') {
            steps {
                cleanWs()              
                script {
                    checkout([$class: 'GitSCM',
                            branches: [[name: "master"]],
                            extensions: [],
                            submoduleCfg: [],
                            userRemoteConfigs: [[url: 'https://github.com/ChayFadida/Terraform.git']]])
                }
            }
        }

        stage('Terraform Init') {
            steps {
                // Initialize Terraform
                script {
                    sh "terraform init"
                }
            }
        }
        
        stage('Terraform Plan') {
            steps {
                // Generate and display Terraform plan
                script {
                    sh "terraform plan -out=tfplan"
                }
            }
        }
        
        stage('Terraform Apply') {
            steps {
                // Apply Terraform changes
                script {
                    sh "terraform apply -auto-approve tfplan"
                }
            }
        }

    }
}