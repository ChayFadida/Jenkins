def JOB_NAME = "pipelines/portfolioRelease"

pipelineJob(JOB_NAME) {
    description 'Portfolio Release Pipeline'

    parameters {
        stringParam('DOCKER_REGISTRY', 'harbor.chay-techs.com', 'Docker registry URL')
        stringParam('APP_NAME', 'portfolio-front', 'Name of your application')
        stringParam('NAMESPACE', 'chay-techs-production', 'Kubernetes namespace')
        stringParam('IMAGE_TAG', '', 'Docker image tag')
    }

    environment {
        DOCKER_IMAGE = 'harbor.chay-techs.com/portfolio/portfolio-front:testim'
        DOCKERFILE_PATH = 'Dockerfile.portfolio'
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
            scriptPath "infrastructure/pipelines/portfolio/JenkinsfileRelease.groovy"
        }
    }
}
