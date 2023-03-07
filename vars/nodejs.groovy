// def lintchecks() {

//     sh "echo installing JSLINT"
//     sh "npm install jslint"
//     sh "ls -ltr node_modules/jslint/bin/"
//     // sh "./node_modules/jslint/bin/jslint.js server.js"
//     sh "echo lint checks done"

// }

def call() {
    node {
            sh "rm -rf *"   // removes all existing files in folders
            git branch: 'main', url: "https://github.com/amrutanubhav/${COMPONENT}.git" // pulls the component from Git
            env.APP = "nodejs"
            common.lintchecks()
            // env.ARGS="-Dsonar.sources=."
            common.sonarchecks()
            common.testCases()
            common.artifacts()


    }

}

// def sonarchecks(COMPONENT) {

//     sh "echo starting code quality analysis"
//     // sh "sonar-scanner -Dsonar.host.url=http://${SONAR_URL}:9000 -Dsonar.sources=. -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW}"
//     sh "echo quality checks done"
//     // sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > sonar-quality-gate.sh"
//     // sh "chmod 777 ./sonar-quality-gate.sh"
//     // sh "sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} ${SONAR_URL} ${COMPONENT}"

// }

/*DECLARATIVE PIPELINE DEFINED BELOW*/

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

//             stages {   // start of stage

//                 stage("Performing Lint checks") {
//                     steps {
//                         script {

//                             lintchecks(COMPONENT)    // if the function is in same file, no need to call with filename as prefix
//                         }
//                     }
//                 }

//                 stage("Performing sonar checks") {
//                     steps {
//                         script {
//                             env.ARGS="-Dsonar.sources=."
//                             common.sonarchecks(COMPONENT)    // if the function is in same file, no need to call with filename as prefix
//                         }
//                     }
//                 }

//                 stage("Performing TEST checks") {
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

//                 stage("Downloading dependencies") {
//                     when { 
//                         expression { env.TAG_NAME ==~ ".*" }
//                         expression { env.UPLOAD_STATUS == "" }
//                         }
//                     steps {
//                         sh "npm install"
//                         sh "zip ${COMPONENT}-${TAG_NAME}.zip node_modules server.js"
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
//                         sh "curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file  ${COMPONENT}-${TAG_NAME}.zip http://${NEXUS_URL}:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip"
//                     }
//                 }
//         } // end of stages
//     }
// }

//commit >> lintcheck >> quality check >> testing  >> nexus 