import infrastructure.dsl.pipeline_job.common.common

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
    common.applyCpsScm(delegate, "bla", "bla", "bla", "jenkins/pipelines/Syssim/Funcsim/Jenkinsfile.KeikoFuncsimPipeline")
}