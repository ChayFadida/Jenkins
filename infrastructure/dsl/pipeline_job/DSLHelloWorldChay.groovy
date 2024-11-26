def JOB_NAME = "pipelines/Hello-World-Chay"

pipelineJob(JOB_NAME) {
    description 'Hello World Chay CI'
    environmentVariables {
        env("GIT_REMOTE", 'https://github.com/ChayFadida/HelloWorldChay.git')
        env('DOCKER_REGISTRY', 'harbor.chay-techs.com')
        env('GIT_USERNAME', 'ChayFadida')
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
            scriptPath "infrastructure/pipelines/helloworld/JenkinsfileHelloWorld.groovy"
        }
    }
}