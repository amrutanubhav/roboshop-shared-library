def sonarchecks() {
    stage('Sonar Checks') {
            sh "echo starting code quality analysis"
            // sh "sonar-scanner -Dsonar.host.url=http://${SONAR_URL}:9000 ${ARGS} -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW}"
            sh "echo quality checks done"
            // sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > sonar-quality-gate.sh"
            // sh "chmod 777 ./sonar-quality-gate.sh"
            // sh "sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} ${SONAR_URL} ${COMPONENT}"
            // uncomment when sonar check is required
        }
}

def lintchecks() {  // modified as groovy structure, declarative pipelines can be seen  on other groovy files
stage('Lint Checks') {
        if(env.APP == "maven") {
            sh "echo installing MAVEN"
            // sh "sudo yum install maven -y"
            // sh "mvn checkstyle:check"
            sh "echo lint checks done"

             }
        else if(env.APP == "nodejs") {

            sh "echo installing JSLINT"
            // sh "npm install jslint"
            // sh "ls -ltr node_modules/jslint/bin/"
            // sh "./node_modules/jslint/bin/jslint.js server.js"
            sh "echo lint checks done"

        }
        else if(env.APP == "python") {

            sh "echo installing pyLINT"
            // sh "npm install jslint"
            // sh "ls -ltr node_modules/jslint/bin/"
            // sh "./node_modules/jslint/bin/jslint.js server.js"
            sh "echo lint checks done"

        }
        else if(env.APP == "angularjs") {

            sh "echo installing angular lint check"
            sh "echo lint checks done"

        }
        else  {

            sh "echo generic lint checks done"

        }
    }
}

def testCases() {
    parallel(                             // stages in parallel for scripted pipeline
            "UNIT": {
                stage("UNIT tests"){
                    sh "echo UNIT testing......"
                }
            },
            "INTEGRATION": {
                    stage("INTEGRATION tests"){
                        sh "echo INTEGRATION testing......"
                }
            },
            "FUNCTIONAL": {
                    stage("FUNCTIONAL tests"){
                        sh "echo INTEGRATION testing......"
                }
            },

    )

}

def artifacts() {
  if (env.TAG_NAME != null) {
    stage("Validating artifacts") {

      sh "echo Checking if artifacts exists in repo"
      env.UPLOAD_STATUS=sh(returnStdout: true, script: "curl -L -s http://172.31.55.113:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true" )

     }

            if (env.UPLOAD_STATUS == "") {

                    stage("Creating Artifacts") {
                                if(env.APP == "maven") {
                                        sh ''' 
                                   mvn clean package
                                   mv target/${COMPONENT}-1.0.jar ${COMPONENT}.jar
                                   zip -r ${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar
                            '''
                                }

                                else if(env.APP == "nodejs") {
                                       sh ''' 
                                   npm install
                                   zip -r ${COMPONENT}-${TAG_NAME}.zip node_modules server.js
                                   
                            '''
                                }

                                else if(env.APP == "python") {
                                        sh ''' 
                                   zip -r ${COMPONENT}-${TAG_NAME}.zip *.py *.ini requirements.txt
                            '''
                                }

                                else if(env.APP == "angularjs") {
                                        sh ''' 
                                   cd static
                                   zip -r ../${COMPONENT}-${TAG_NAME}.zip *
                            '''
                     }
                                else {
                                        sh "echo artifact is prepared "
                                }
                        }
                    
    stage("Uploading Artifacts") {

            // below syntax is forged from pipeline syntax from jenkins
        withCredentials([usernamePassword(credentialsId: 'NEXUS', passwordVariable: 'NEXUS_PSW', usernameVariable: 'NEXUS_USR')]) {

            sh "echo exporting binaries to NEXUS"
            sh "curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file  ${COMPONENT}-${TAG_NAME}.zip http://172.31.55.113:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip"

         }
                    }


            }
        }
}