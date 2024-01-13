import infrastructure/dsl/pipeline_job/common/common

def JOB_NAME = "pipelines/Keiko-Funcsim-Pipeline"

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
    common.applyCpsScm(delegate, Constants.SCM_URL_KEIKO_JOB_DSL_REPO, Constants.SCM_CREDENTIAL_ID_KEIKO_REPO, branch_name, "jenkins/pipelines/Syssim/Funcsim/Jenkinsfile.KeikoFuncsimPipeline")
}