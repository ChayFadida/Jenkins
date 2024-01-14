def JOB_NAME = "pipelines/alignIp"

pipelineJob(JOB_NAME) {
    description 'Portfolio Release Pipeline'

    definition {
        cpsScm {
            scm {
                git {
                    branch 'chay/seedall' // Replace with your branch name
                    remote {
                        url 'https://github.com/ChayFadida/Jenkins.git' // Replace with your Git repository URL
                    }
                }
            }
            scriptPath "infrastructure/pipelines/alignIp/JenkinsfileAlignIp.groovy"
        }
    }
}
