def JOB_NAME = "pipelines/portfolioRelease"

pipelineJob(JOB_NAME) {
    description 'Portfolio Release Pipeline'

    parameters {
        stringParam('DOCKER_REGISTRY', 'harbor.chay-techs.com', 'Docker registry URL')
        stringParam('APP_NAME', 'portfolio-front', 'Name of your application')
        stringParam('NAMESPACE', 'chay-techs-production', 'Kubernetes namespace')
        stringParam('IMAGE_TAG', '', 'Docker image tag')
    }

    definition {
        cpsScm {
            scm {
                git {
                    branch branchName // Replace with your branch name
                    remote {
                        url 'https://github.com/ChayFadida/Jenkins.git' // Replace with your Git repository URL
                    }
                }
            }
            scriptPath "infrastructure/pipelines/portfolio/JenkinsfileRelease.groovy"
        }
    }
}
