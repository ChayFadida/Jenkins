def JOB_NAME = "pipelines/Portfolio-CI"

pipelineJob(JOB_NAME) {
    description 'Portfolio CI Pipeline'
    triggers {
        genericTrigger {
            genericVariables {
                genericVariable {
                    key("portfolio_branch")
                    value("\$.ref")
                }
            }
            regexpFilterText("\$portfolio_branch")
            regexpFilterExpression("^(refs\\/heads\\/(master|develop))*?\$")
            printContributedVariables(true)
            printPostContent(true)
            tokenCredentialId('Portfolio-CI-Webhook-Token')
        }
    }

    environmentVariables {
        env('DOCKER_REGISTRY', 'harbor.chay-techs.com')
        env('GIT_USERNAME', 'ChayFadida')
        env('GIT_MAIL', 'chayfadida1997@gmail.com')
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
