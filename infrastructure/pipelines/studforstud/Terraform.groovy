pipeline {
    agent {
        kubernetes {
            label 'K8S-With-Docker'
        }
    }
    environment {
        TF_VAR_zone_id = credentials('TF_VAR_zone_id')
        TF_VAR_domain = credentials('TF_VAR_domain')
        TF_VAR_token = credentials('TF_VAR_token')
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
                        def terraformApplyOutput = sh(
                            script: "terraform apply -auto-approve tfplan 2>&1 || true", 
                            returnStdout: true
                        ).trim()

                        // Check if the output contains the specific error message
                        if (terraformApplyOutput.contains("expected DNS record to not already be present but already exists")) {
                            echo "Skipping Terraform apply because DNS record already exists"
                        } else {
                            // If the error message is not found, print the Terraform apply output
                            echo "Terraform apply output: ${terraformApplyOutput}"
                        }
                    }
                }
            }
        }

    }
}