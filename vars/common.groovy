def sonarchecks(COMPONENT) {
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

def lintchecks() {
stage('Lint Checks')
        if(env.APP == "maven") {
            sh "echo installing MAVEN"
            // sh "sudo yum install maven -y"
            // sh "mvn checkstyle:check"
            sh "echo lint checks done"

             }
        else if(env.APP == "nodejs") {

            sh "echo installing JSLINT"
            sh "npm install jslint"
            sh "ls -ltr node_modules/jslint/bin/"
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

def testCases() {
    parallel(                             // stages in parallel for scripted pipeline
            "UNIT": {
                stage("UNIT tests"){
                    sh "echo UNIT testing......"
            }
        }
            "INTEGRATION": {
                    stage("INTEGRATION tests"){
                        sh "echo INTEGRATION testing......"
                }
            }
            "FUNCTIONAL": {
                    stage("FUNCTIONAL tests"){
                        sh "echo INTEGRATION testing......"
                }
            }

    )

}

def artifacts() {

    stage("Validating artifacts") {

      sh "echo Checking if artifacts exists in repo"
      env.UPLOAD_STATUS=sh(returnStdout: true, script: "curl -L -s http://${NEXUS_URL}:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true" )

     }

            if (env.UPLOAD_STATUS == "") {

                    stage("Exporting to nexus repository") {
                                if(env.APP == "maven") {
                                        sh "mvn clean package"
                                        sh "mv ./target/${COMPONENT}-1.0.jar ${COMPONENT}.jar"
                                        sh "zip ${COMPONENT}-${TAG_NAMe}.zip ${COMPONENT}.jar "
                                        sh "ls -ltr"
                                }

                                else if(env.APP == "nodejs") {
                                        sh "npm install"
                                        sh "zip ${COMPONENT}-${TAG_NAME}.zip node_modules server.js"
                                        sh "ls -ltr"
                                }

                                else if(env.APP == "python") {
                                        sh "zip -r ${COMPONENT}-${TAG_NAME}.zip *.ini *.py requirements.txt"
                                        sh "ls -ltr"
                                }

                                else if(env.APP == "python") {
                                        sh "zip -r ${COMPONENT}-${TAG_NAME}.zip "
                                        sh "ls -ltr"
                                }
                                else {
                                        sh "echo artifact is prepared "
                                }
            }
    }
}
