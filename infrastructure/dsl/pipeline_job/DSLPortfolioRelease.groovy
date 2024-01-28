def JOB_NAME = "pipelines/Portfolio-CI"

pipelineJob(JOB_NAME) {
    description 'Portfolio CI Pipeline'
    if(branch_name == 'chay/placeholder'){
        triggers {
            genericTrigger {
                regexpFilterExpression('chay/placeholder')
                token('PORTFOLIO-CI')
            }
        }
    }
    environmentVariables {
        env('DOCKER_REGISTRY', 'https://harbor.chay-techs.com')
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
