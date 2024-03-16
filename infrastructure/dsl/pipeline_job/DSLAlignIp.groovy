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
                    branch "chay/portfolio_release"
                    remote {
                        url 'https://github.com/ChayFadida/Jenkins.git'
                    }
                }
            }
            scriptPath "infrastructure/pipelines/alignIp/JenkinsfileAlignIp.groovy"
        }
    }
}
