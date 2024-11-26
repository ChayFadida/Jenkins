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
        stage('Bump Patch Version') {
            steps {
                dir('myapp') {
                    script {
                        // Increment patch version in the pom.xml
                        def pom = readMavenPom file: 'pom.xml'
                        def version = pom.version.tokenize('.')
                        version[-1] = (version[-1].toInteger() + 1).toString()
                        def newVersion = version.join('.')
                        sh """
                        mvn versions:set -DnewVersion=${newVersion} -DgenerateBackupPoms=false
                        """
                        echo "Updated version to ${newVersion}"
                    }
                }
            }
        }
    }
}
