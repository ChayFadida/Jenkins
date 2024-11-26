def JOB_NAME = "pipelines/balb"

pipelineJob(JOB_NAME) {
    description 'Align Stud-For-Stud Ip With CloudFlare Pipeline'


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
            scriptPath "infrastructure/pipelines/alignIp/JenkinsfileAlignIpStudForStud.groovy"
        }
    }
}
