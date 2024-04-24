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
                dir("cloudflare"){
                    script {
                        sh "terraform init"
                    }
                }
            }
        }
        
        stage('Terraform Plan') {
            steps {
                dir("cloudflare"){
                    script {
                        sh "terraform plan -out=tfplan"
                    }
                }
            }
        }
        
        stage('Terraform Apply') {
            steps {
                dir("cloudflare"){
                    script {
                        sh "terraform apply -auto-approve tfplan"
                    }
                }
            }
        }

    }
}