def JOB_NAME = "pipelines/alignIp-Stud-For-Stud"

pipelineJob(JOB_NAME) {
    description 'Align Stud-For-Stud Ip With CloudFlare Pipeline'
    
    triggers {
        cron('*/5 * * * *') // Schedule the job to run every 5 minutes
    }

    definition {
        cpsScm {
            scm {
                git {
                    branch branchName
                    remote {
                        url 'https://github.com/ChayFadida/Jenkins.git'
                    }
                }
            }
            scriptPath "infrastructure/pipelines/alignIp/JenkinsfileAlignIpStudForStud.groovy"
        }
    }
}
