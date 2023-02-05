def lintchecks(COMPONENT) {

    sh "echo installing MAVEN"
    sh "yum install maven -y"
    sh "mvn checkstyle:check"
    sh "echo lint checks done"

}


def call(COMPONENT)    // call is the default functions that is called
{
        pipeline {
            agent any
            stages {   // start of stages

                stage("Performing Lint checks") {
                    steps {
                        script {

                            lintchecks(COMPONENT)    // if the function is in same file, no need to call with filename as prefix
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

