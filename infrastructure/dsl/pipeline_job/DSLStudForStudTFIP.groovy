import com.utils
def JOB_NAME = "pipelines/terraformStud"
CommonSteps.createPathFolder(this, 'pipelines')
pipelineJob(JOB_NAME) {
    description 'StudForStud CI Pipeline'

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
            scriptPath "infrastructure/pipelines/studforstud/Terraform.groovy"
        }
    }
}
