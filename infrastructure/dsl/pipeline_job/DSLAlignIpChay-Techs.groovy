def JOB_NAME = "pipelines/alignIp-Chay-Techs"

pipelineJob(JOB_NAME) {
    description 'Align Chay-Techs Ip With CloudFlare Pipeline'
    
    triggers {
        cron('*/5 * * * *') // Schedule the job to run every 5 minutes
    }

    definition {
        cpsScm {
            scm {
                git {
                    branch "studforstud"
                    remote {
                        url 'https://github.com/ChayFadida/Jenkins.git'
                    }
                }
            }
            scriptPath "infrastructure/pipelines/alignIp/JenkinsfileAlignIpChay-Techs.groovy"
        }
    }
}
