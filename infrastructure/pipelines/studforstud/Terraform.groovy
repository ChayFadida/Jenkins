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
                sh "ls"
                sh "pwd"
                dir("CloudFlare"){
                    script {
                        sh "terraform init"
                    }
                }
            }
        }
        
        stage('Terraform Plan') {
            steps {
                dir("CloudFlare"){
                    script {
                        sh "terraform plan -out=tfplan"
                    }
                }
            }
        }
        
        stage('Terraform Apply') {
            steps {
                dir("CloudFlare"){
                    script {
                        sh "terraform apply -auto-approve tfplan"
                    }
                }
            }
        }

    }
}