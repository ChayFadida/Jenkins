
def JOB_NAME = "pipelines/alignIp"

pipelineJob(JOB_NAME) {
    description 'Portfolio Release Pipeline'

    properties {
        buildDiscarderProperty {
            strategy {
                logRotator {
                    numToKeep(10)  // Number of builds to keep
                }
            }
        }
    }

    cpsScm {
        scm {
            git {
                branch 'master' // Replace with your branch name
                remote {
                    url 'https://github.com/ChayFadida/Utils.git' // Replace with your Git repository URL
                }
            }
        }
    }
    
    
}