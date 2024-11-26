def JOB_NAME = "pipelines/Hello-World-Chay"

pipelineJob(JOB_NAME) {
    description 'Hello World Chay CI'
    environmentVariables {
        env(HELLO_WORLD_CHAY_GITHUB_URL, "https://github.com/ChayFadida/HelloWorldChay.git")
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
