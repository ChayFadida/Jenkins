import com.utils
def JOB_NAME = "pipelines/Hello-World-Chay-CI"
CommonSteps.createPathFolder(this, 'pipelines')
pipelineJob(JOB_NAME) {
    description 'CI for hello world chay application'
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
                        url Constants.HELLO_WORLD_CHAY_GITHUB_URL
                    }
                }
            }
            scriptPath "infrastructure/pipelines/alignIp/JenkinsfileAlignIpStudForStud.groovy"
        }
    }
}
