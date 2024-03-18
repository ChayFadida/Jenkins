pipeline {
    agent {
        kubernetes {
            label 'jenkins-jenkins-agent'
        }
    }
    environment {
        EMAIL_CREDENTIAL = credentials('EMAIL_CREDENTIAL')
        API_KEY_CREDENTIAL = credentials('API_KEY_CREDENTIAL')
        ZONE_IDENTIFIER_CREDENTIAL = credentials('ZONE_IDENTIFIER_CREDENTIAL')
    }
    stages {
        stage('Checkout Source Code') {
            steps {
                script {
                    checkout([$class: 'GitSCM',
                              branches: [[name: 'master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions: [],
                              submoduleCfg: [],
                              userRemoteConfigs: [[url: 'https://github.com/ChayFadida/Utils.git']]])
                }
            }
        }
        stage('Run Script') {
            steps {
                sh 'chmod +x ./change_domains_ip.sh' // Make the script executable
                sh "./change_domains_ip.sh '${EMAIL_CREDENTIAL}' '${API_KEY_CREDENTIAL}' '${ZONE_IDENTIFIER_CREDENTIAL}' 'chay-techs.com'"
            }
        }
    }
}
