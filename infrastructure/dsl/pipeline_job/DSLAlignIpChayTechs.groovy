import com.utils.CommonSteps
def JOB_NAME = "pipelines/alignIp-Chay-Techs"

CommonSteps.createPathFolder(this, 'pipelines')
pipelineJob(JOB_NAME) {
    description 'Align Chay-Techs Ip With CloudFlare Pipeline'
    if (productionEnv == true) {
        triggers {
            cron('*/5 * * * *') // Schedule the job to run every 5 minutes
        }
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
            scriptPath "infrastructure/pipelines/alignIp/JenkinsfileAlignIpChay-Techs.groovy"
        }
    }
}
