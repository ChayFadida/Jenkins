pipeline {
    agent {
        kubernetes {
            label 'K8S-With-Docker-Maven'
        }
    }
    environment {
        IMAGE_TAG = ''
        IMAGE_REPO = ''
        HELM_VALUES_FILE = ''
    }
    stages {
        stage('Checkout Source Code') {
            steps {
                cleanWs()
                dir('hello-world-src') {
                    script {
                        def gitInfo = checkout([$class: 'GitSCM',
                                branches: [[name: fullBranchName]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: GIT_SRC_REMOTE]]])
                        if (branchName == 'master') {
                            IMAGE_REPO = 'prod'
                            HELM_VALUES_FILE = 'values-prod.yaml'
                        } else if (branchName == 'staging') {
                            IMAGE_REPO = 'staging'
                            HELM_VALUES_FILE = 'values-staging.yaml'
                        } else {
                            IMAGE_REPO = 'dev'
                        }
                    }
                }

                dir('gitops') {
                    script {
                        checkout([$class: 'GitSCM',
                                branches: [[name: 'master']],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: GITOPS_REMOTE]]])
                        sh "git checkout master"
                    }
                }
            }
        }

        // stage('Run Maven Tests') {
        //     steps {
        //         dir('myapp') {
        //             sh "mvn clean test -DskipTests=false"
        //         }
        //     }
        // }

        // stage('Publish Test Results') {
        //     steps {
        //         junit 'myapp/target/**/TEST-*.xml'
        //     }
        // }

        // stage('Bump Patch Version') {
        //     steps {
        //         dir('myapp') {
        //             script {
        //                 // Increment patch version in the pom.xml
        //                 def pom = readMavenPom file: 'pom.xml'
        //                 def version = pom.version.tokenize('.')
        //                 version[-1] = (version[-1].toInteger() + 1).toString()
        //                 env.NEW_VERSION = version.join('.')
        //                 sh """
        //                     mvn versions:set -DnewVersion=${NEW_VERSION} -DgenerateBackupPoms=false
        //                 """
        //                 echo "Updated version to ${NEW_VERSION}"
        //             }
        //         }
        //     }
        // }
        // stage('Compile Project') {
        //     steps {
        //         dir('myapp') {
        //             sh "mvn compile"
        //         }
        //     }
        // }
        // stage('Package Project') {
        //     steps {
        //         dir('myapp') {
        //             sh "mvn package -DskipTests"
        //         }
        //     }
        // }
        // stage('Publish Artifacts') {
        //     steps {
        //         dir('myapp') {
        //             archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        //         }
        //     }
        // }

        stage('Build Docker Image') {
            steps {
                dir('hello-world-src'){
                    dir('myapp') {
                        script {
                            def commitHash = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                            def sanitizedBranch = fullBranchName.replaceAll('/', '_')
                            IMAGE_TAG = "${sanitizedBranch}_${commitHash}"
                            def docker_build_params = "--label 'app.branch=${fullBranchName}' --label 'app.commit=${commitHash}'"
                            docker.withRegistry("https://${dockerRegistry}", 'harbor-cred-secret') {
                                def docker_image = docker.build("${dockerRegistry}/hello-world/${IMAGE_REPO}:${IMAGE_TAG}", "${docker_build_params} --no-cache -f Dockerfile .")
                                    docker_image.push()
                                    sh "docker rmi ${docker_image.id}"
                            }
                        }
                    }
                }
            }
        }

        stage('Update GitOps values') {
            when {
                expression {
                    branchName == 'master' || branchName == 'staging'
                }
            }
            steps {
                dir('gitops') {
                    script {     
                        // Read the Deployment YAML file
                        def helm_values_path = "hello-world-app/${HELM_VALUES_FILE}"
                        def helm_values = readYaml file: helm_values_path

                        // Update the image tag in the Deployment YAML
                        helm_values.image.tag = IMAGE_TAG

                        // Write the modified Deployment YAML back to the file
                        writeYaml file: helm_values_path, data: helm_values, overwrite: true

                        withCredentials([usernamePassword(credentialsId: 'github-secret-login', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh """
                                git config --global user.email ${GIT_MAIL}"
                                git config --global user.name ${GIT_USERNAME}"
                                git commit -am "Update Docker image tag in ${HELM_VALUES_FILE}
                                git push https://$USERNAME:$PASSWORD@github.com/ChayFadida/HelloWorldChayGitOps.git"
                            """
                        }
                    }
                }
            }
        }

    }
}
