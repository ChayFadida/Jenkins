def JOB_NAME = "pipelines/Hello-World-Chay"

pipelineJob(JOB_NAME) {
    description 'Hello World Chay CI'
    triggers {
        genericTrigger {
            genericVariables {
                genericVariable {
                    key("fullBranchName")
                    value("\$.ref")
                }
                key("portfolio_branch")
                    value("\$.ref")
                    regexpFilter("^(refs\\/heads\\/)")
                }
            }
            regexpFilterText("\$fullBranchName")
            regexpFilterExpression("^(refs\\/heads\\/(master|staging))*?\$")
            printContributedVariables(true)
            printPostContent(true)
            tokenCredentialId('HelloWorld-CI-Webhook-Token')
        }
    }

    environmentVariables {
        env("GIT_SRC_REMOTE", 'https://github.com/ChayFadida/HelloWorldChay.git')
        env("GITOPS_REMOTE", 'https://github.com/ChayFadida/HelloWorldChayGitOps.git')
        env('DOCKER_REGISTRY', 'harbor.chay-techs.com')
        env('GIT_USERNAME', 'ChayFadida')
    }

    parameters {
        stringParam('dockerRegistry', 'harbor.chay-Techs.com', 'Branch to build')
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
