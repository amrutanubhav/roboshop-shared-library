def call() {
    node {
            sh "rm -rf *"   // removes all existing files in folders
            git branch: 'main', url: "https://github.com/amrutanubhav/${COMPONENT}.git" // pulls the component from Git 
            env.APP = "angularjs"
            common.lintchecks()
            // env.ARGS="-Dsonar.sources=."
            common.sonarchecks()
            common.testCases()
            common.artifacts()

    }

}