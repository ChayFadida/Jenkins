class CommonSteps {
    static void applyCpsScm(job, String scmUrl, String credentialsId, String branchName, String scriptFilePath) {
        job.definition {
            cpsScm {                
                scm {
                    git {
                        branch branchName
                        remote {
                            url scmUrl
                            credentials credentialsId
                        }
                    }
                }
                scriptPath(scriptFilePath)
            }
        }
    }
}