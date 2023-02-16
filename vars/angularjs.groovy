def call() {
    node {
            sh "rm -rf *"
            git branch: 'main', url: "https://github.com/amrutanubhav/${COMPONENT}.git" 
            env.APP == "angularjs"
            common.lintchecks()
            env.ARGS="-Dsonar.java.binaries=target/"
            common.sonarchecks()
            common.testCases()
            common.artifacts()

    }

}