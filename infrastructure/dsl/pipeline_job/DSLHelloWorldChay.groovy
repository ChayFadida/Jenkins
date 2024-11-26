def JOB_NAME = "pipelines/Hello-World-Chay"

pipelineJob(JOB_NAME) {
    description 'Hello World Chay CI'
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
            scriptPath "infrastructure/pipelines/helloworld/JenkinsfileHelloWorld.groovy"
        }
    }
}
