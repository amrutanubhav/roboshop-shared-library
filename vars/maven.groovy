def lintchecks(COMPONENT) {

    sh "echo installing MAVEN"
    // sh "sudo yum install maven -y"
    // sh "mvn checkstyle:check"
    sh "echo lint checks done"

}

def sonarchecks(COMPONENT) {

    sh "echo starting code quality analysis"
    sh "mvn clean compile"
    sh "sonar-scanner -Dsonar.host.url=http://${SONAR_URL}:9000 -Dsonar.java.binaries=target/ -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW}"
    sh "echo quality checks done"
    sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > sonar-quality-gate.sh"
    sh "chmod 777 ./sonar-quality-gate.sh"
    sh "sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} ${SONAR_URL} ${COMPONENT}"

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
                        sh "mvn clean package"
                    }
                }
        } // end of stages
    }
}

