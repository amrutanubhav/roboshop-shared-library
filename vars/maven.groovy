// def lintchecks(COMPONENT) {
//     stage('Lint Checks') {
//         sh "echo installing MAVEN"
//         // sh "sudo yum install maven -y"
//         // sh "mvn checkstyle:check"
//         sh "echo lint checks done"

//     }

// }


def call() {
    node {
            sh "rm -rf *"
            git branch: 'main', url: "https://github.com/amrutanubhav/${COMPONENT}.git" 
            env.APP == "maven"
            common.lintchecks()
            sh "mvn clean compile"
            env.ARGS="-Dsonar.java.binaries=target/"
            common.sonarchecks()
            common.testCases()
            common.artifacts()

    }

}


// def sonarchecks(COMPONENT) {

//     sh "echo starting code quality analysis"
//     sh "mvn clean compile"
//     sh "sonar-scanner -Dsonar.host.url=http://${SONAR_URL}:9000 ${ARGS} -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW}"
//     sh "echo quality checks done"
//     sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > sonar-quality-gate.sh"
//     sh "chmod 777 ./sonar-quality-gate.sh"
//     sh "sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} ${SONAR_URL} ${COMPONENT}"

// }

/* defined is decclarative pipelines */

// def call(COMPONENT)    // call is the default functions that is called
// {
//         pipeline {
//             agent any
//             environment {

//                 SONAR = credentials('SONAR')
//                 SONAR_URL = "172.31.6.105"
//                 NEXUS = credentials('NEXUS')
//                 NEXUS_URL = "172.31.15.122"
        
//                }
//             stages {   // start of stages

//                 stage("Performing Lint checks") {
//                     steps {
//                         script {

//                             lintchecks(COMPONENT)    // if the function is in same file, no need to call with filename as prefix
//                         }
//                     }
//                 }


//                 stage("Performing sonar checks") {
//                         steps {
//                             script {
//                                 sh "mvn clean compile" 
//                                 env.ARGS="-Dsonar.java.binaries=target/"
//                                 common.sonarchecks(COMPONENT)    // if the function is in same file, no need to call with filename as prefix
//                             }
//                         }
//                     }


//                 stage("Performing TESTS") {
//                         parallel {
//                             stage("Unit tests") {
//                                steps {
//                                  sh "echo unit testing......"
//                                }
//                             }
//                             stage("Integration tests") {
//                                steps {
//                                  sh "echo Integration testing......"
//                                }
//                             }
//                             stage("Functional tests") {
//                                steps {
//                                  sh "echo Functional testing......"
//                                }
//                             }
//                     }
//                 }

//                 stage("Validating artifacts") {
//                     when { 
//                         expression { env.TAG_NAME ==~ ".*" } 
//                         }
//                     steps {
//                         sh "echo Checking if artifacts exists in repo"
//                         script {
                        
//                         env.UPLOAD_STATUS=sh(returnStdout: true, script: "curl -L -s http://${NEXUS_URL}:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true" )
//                         print UPLOAD_STATUS

//                         }
//                     }
//                 }

//                 stage("Creating dependencies and sending to nexus repository") {
//                     when { 
//                         expression { env.TAG_NAME ==~ ".*" }
//                         expression { env.UPLOAD_STATUS == "" }
//                         }
//                     steps {
//                         sh "mvn clean package"
//                         sh "mv ./target/${COMPONENT}-1.0.jar ${COMPONENT}.jar"
//                         sh "zip ${COMPONENT}-${TAG_NAMe}.zip ${COMPONENT}.jar "
//                         sh "ls -ltr"
                        
//                     }
//                 }

//                 stage("Exporting to nexus repository") {
//                     when { 
//                         expression { env.TAG_NAME ==~ ".*" }
//                         expression { env.UPLOAD_STATUS == "" }
//                         }
//                     steps {
//                         sh "echo exporting binaries to NEXUS"
//                         sh "curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${COMPONENT}-${TAG_NAMe}.zip http://${NEXUS_URL}:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAMe}.zip"
//                     }
//                 }

            
//         } // end of stages

// }
// }