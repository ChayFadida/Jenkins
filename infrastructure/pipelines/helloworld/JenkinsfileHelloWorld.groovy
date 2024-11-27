pipeline {
    agent {
        kubernetes {
            label 'K8S-With-Docker-Maven'
        }
    }
    environment {
        MAVEN_HOME = '/opt/apache-maven-3.9.9/bin/mvn'  // Replace with your actual Maven directory
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
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

        stage('Run Maven Tests') {
            steps {
                dir('myapp') {
                    sh "mvn clean test -DskipTests=false"
                }
            }
        }

        stage('Publish Test Results') {
            steps {
                junit 'myapp/target/**/TEST-*.xml'
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
