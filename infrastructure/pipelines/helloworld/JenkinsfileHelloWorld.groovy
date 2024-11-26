pipeline {
    agent {
        kubernetes {
            label 'jenkins-jenkins-agent'
        }
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
                              userRemoteConfigs: [[url: GIT_REMOTE]]])
                }
            }
        }
    }
}
