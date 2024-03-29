pipeline {
    agent {
        kubernetes {
            label 'K8S-With-Docker'
        }
    }
    environment {
        CHECKED_OUT_BRANCH = ''
        IMAGE_TAG = ''
    }
    stages {
        stage('Checkout Source Code') {
            steps {
                cleanWs()
                dir('studforstud-src') {
                    script {
                        def gitInfo = checkout([$class: 'GitSCM',
                                branches: [[name: studforstud_branch]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/StudForStud.git']]])
                        CHECKED_OUT_BRANCH = gitInfo.GIT_BRANCH.split('/')[-1]
                    }
                }

                dir('studforstud-cd') {
                    script {
                        checkout([$class: 'GitSCM',
                                branches: [[name: studforstud_branch]],
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs: [[url: 'https://github.com/ChayFadida/StudForStudGitOps.git']]])
                        sh "git checkout ${CHECKED_OUT_BRANCH}"
                    }
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                dir('studforstud-src') {
                    sh "ls"
                    script {
                        def commitHash = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                        IMAGE_TAG = "${CHECKED_OUT_BRANCH}_${commitHash}"
                        docker.withRegistry("https://${DOCKER_REGISTRY}", 'harbor-cred-secret') {
                            def docker_image = docker.build("${DOCKER_REGISTRY}/studforstud/studforstud-app:${IMAGE_TAG}", "-f Dockerfile .")
                            docker_image.push()
                            sh "docker rmi ${docker_image.id}"
                        }
                        
                    }
                }
            }
        }

        
        stage('Update Deployment') {
            steps {
                dir('studforstud-cd') {
                    script {
                        def deploymentPaths
                        // Define an array of file paths
                        if (CHECKED_OUT_BRANCH == "master") {
                            deploymentPaths = ["environments/${CHECKED_OUT_BRANCH}/deployment.yml", "environments/${CHECKED_OUT_BRANCH}/cron-backup-cloud.yml"]
                        } else {
                            deploymentPaths = ["environments/${CHECKED_OUT_BRANCH}/deployment.yml"]
                        }
                        // Iterate over each deployment path
                        for (def deploymentPath in deploymentPaths) {
                            // Read the Deployment YAML file
                            def deploymentYaml = readFile(deploymentPath)

                            // Update the image tag in the Deployment YAML
                            deploymentYaml = deploymentYaml.replaceAll("(?<=image: harbor\\.chay-techs\\.com\\/studforstud\\/studforstud-app:)\\S+", IMAGE_TAG)
                            deploymentYaml = deploymentYaml.replaceAll("(?<=- name: APP_VERSION\\n {14}value: \\\\?\")[^\"]+", IMAGE_TAG)

                            // Write the modified Deployment YAML back to the file
                            writeFile(file: deploymentPath, text: deploymentYaml)

                            // Add the file to Git staging
                            sh "git add ${deploymentPath}"
                        }

                        // Commit changes to Git
                        withCredentials([usernamePassword(credentialsId: 'github-secret-login', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh "git config --global user.email ${GIT_MAIL}"
                            sh "git config --global user.name ${GIT_USERNAME}"
                            sh 'git commit -m "Update Docker image for all yml files"'
                            sh "git push https://$USERNAME:$PASSWORD@github.com/ChayFadida/StudForStudGitOps.git"
                        }
                    }
                }
            }
        }

    }

    post {
        success {
            echo 'Pipeline completed successfully. Pushed changes to the ArgoCD Git repository.'
        }
    }
}