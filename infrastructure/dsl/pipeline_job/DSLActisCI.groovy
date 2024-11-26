def JOB_NAME = "pipelines/Actis-CI"

pipelineJob(JOB_NAME) {
    description 'Actis CI Pipeline'
    if (productionEnv == true) {
        triggers {
            genericTrigger {
                genericVariables {
                    genericVariable {
                        key("actis_branch")
                        value("\$.ref")
                    }
                }
                regexpFilterText("\$actis_branch")
                regexpFilterExpression("^(refs\\/heads\\/(master|develop))*?\$")
                printContributedVariables(true)
                printPostContent(true)
                tokenCredentialId('Actis-CI-Webhook-Token')
            }
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
            scriptPath "infrastructure/pipelines/actis/JenkinsActis.groovy"
        }
    }
}
