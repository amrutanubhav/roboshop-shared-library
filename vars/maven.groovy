def lintchecks(COMPONENT) {

    sh "echo installing MAVEN"
    // sh "yum install maven -y"
    // sh "mvn checkstyle:check"
    sh "echo lint checks done"

}

def sonarchecks(COMPONENT) {

    sh "echo starting code quality analysis"
    sh "sonar-scanner -Dsonar.host.url=http://$(SONAR_URL):9000 -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW}"
    sh "echo quality checks done"

}

def call(COMPONENT)    // call is the default functions that is called
{
        pipeline {
            agent any
            environment {

                SONAR = credentials('SONAR')
                SONAR_URL = "172.31.6.105"
        
               }
            stages {   // start of stages

                stage("Performing Lint checks") {
                    steps {
                        script {

                            lintchecks(COMPONENT)    // if the function is in same file, no need to call with filename as prefix
                        }
                    }
                }


            stage("Performing sonar checks") {
                    steps {
                        script {

                            sonarchecks(COMPONENT)    // if the function is in same file, no need to call with filename as prefix
                        }
                    }
                }


                stage("Downloading dependencies") {
                    steps {
                        sh "echo mvn clean package"
                    }
                }
        } // end of stages
    }
}

