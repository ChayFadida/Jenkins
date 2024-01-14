def JOB_NAME = "pipelines/alignIp"

pipelineJob(JOB_NAME) {
    description 'Align Ip With CloudFlare Pipeline'
    
    triggers {
        cron('*/5 * * * *') // Schedule the job to run every 5 minutes
    }

    definition {
        cpsScm {
            scm {
                git {
                    branch branchName // Replace with your branch name
                    remote {
                        url 'https://github.com/ChayFadida/Jenkins.git' // Replace with your Git repository URL
                    }
                }
            }
            scriptPath "infrastructure/pipelines/alignIp/JenkinsfileAlignIp.groovy"
        }
    }
}
