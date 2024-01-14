def JOB_NAME = "pipelines/alignIp"

pipelineJob(JOB_NAME) {
    description 'Portfolio Release Pipeline'

    definition {
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
        scriptPath("infrastructure/pipelines/alignIp/JenkinsfileAlignIp.groovy")
    }
}
