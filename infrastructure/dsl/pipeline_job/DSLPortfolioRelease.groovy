def JOB_NAME = "pipelines/portfolioRelease"

pipelineJob(JOB_NAME) {
    description 'Portfolio Release Pipeline'

    parameters {
        stringParam('DOCKER_REGISTRY', 'https://harbor.chay-techs.com', 'Docker registry URL')
        stringParam('IMAGE_TAG', '', 'Docker image tag')
    }

    enviromentVariables {
        env('DOCKER_REGISTRY', 'harbor.chay-techs.com')
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
            scriptPath "infrastructure/pipelines/portfolio/JenkinsfileRelease.groovy"
        }
    }
}
