def JOB_NAME = "pipelines/Portfolio-CI"

pipelineJob(JOB_NAME) {
    description 'Portfolio CI Pipeline'
    triggers {
        genericTrigger {
            genericVariables {
                genericVariable {
                    key("portfolio_branch")
                    value("\$.ref")
                    regexpFilter("refs/heads/(.*)")  // Apply the regular expression here
                    defaultValue("")
                    regexpFilterText("\$.portfolio_branch == 'chay/kubernetes'")
                }
            }
            token('PORTFOLIO-CI')
        }
    }

    environmentVariables {
        env('DOCKER_REGISTRY', 'https://harbor.chay-techs.com')
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
