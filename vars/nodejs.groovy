def lintchecks() {

    sh "echo installing JSLINT"
    sh "npm install jslint"
    sh "ls -ltr node_modules/jslint/bin/"
    sh "./node_modules/jslint/bin/jslint.js server.js"
    sh "echo lint checks done"

}


def call()    // call is the default functions that is called
{
        pipeline {
            agent any
            stages {   // start of stages

                stage("Performing Lint checks") {
                    steps {
                        script {

                            lintchecks()    // if the function is in same file, no need to call with filename as prefix
                        }
                    }
                }
                stage("Downloading dependencies") {
                    steps {
                        sh "npm install"
                    }
                }
        } // end of stages
    }
}

