import com.utils
def JOB_NAME = "pipelines/StudForStud-CI"
CommonSteps.createPathFolder(this, 'pipelines')
pipelineJob(JOB_NAME) {
    description 'StudForStud CI Pipeline'
    if (productionEnv == true) {
        triggers {
            genericTrigger {
                genericVariables {
                    genericVariable {
                        key("studforstud_branch")
                        value("\$.ref")
                    }
                }
                regexpFilterText("\$studforstud_branch")
                regexpFilterExpression("^(refs\\/heads\\/(master|develop))*?\$")
                printContributedVariables(true)
                printPostContent(true)
                tokenCredentialId('StudForStud-CI-Webhook-Token')
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
            scriptPath "infrastructure/pipelines/studforstud/JenkinsfileCI.groovy"
        }
    }
}
