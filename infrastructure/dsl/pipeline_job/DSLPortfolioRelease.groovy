def JOB_NAME = "pipelines/Keiko-Funcsim-Pipeline"
def commonSteps = load "common/common.groovy"

pipelineJob(JOB_NAME) {
    description 'Portfolio Release Pipeline'

    properties {
        githubProjectUrl("https://github.com/ChayFadida/Portfolio.git")
        buildDiscarderProperty {
            strategy {
                logRotator {
                    numToKeep(10)  // Number of builds to keep
                }
            }
        }
    }

    // Pipeline triggers
    // CommonSteps.applyKeikoPullRequestTrigger(delegate)   

    // Pipeline definition
   // commonSteps.applyCpsScm(delegate, Constants.SCM_URL_KEIKO_JOB_DSL_REPO, Constants.SCM_CREDENTIAL_ID_KEIKO_REPO, branch_name, "jenkins/pipelines/Syssim/Funcsim/Jenkinsfile.KeikoFuncsimPipeline")
}