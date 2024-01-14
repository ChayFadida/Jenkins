def JOB_NAME = "pipelines/portfolioRelease"

pipelineJob(JOB_NAME) {
    description 'Portfolio Release Pipeline'

    parameters {
        stringParam(name: 'DOCKER_REGISTRY', defaultValue: 'harbor.chay-techs.com', description: 'Docker registry URL')
        stringParam(name: 'APP_NAME', defaultValue: 'portfolio-front', description: 'Name of your application')
        stringParam(name: 'NAMESPACE', defaultValue: 'chay-techs-production', description: 'Kubernetes namespace')
        stringParam(name: 'IMAGE_TAG', description: 'Docker image tag')
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
