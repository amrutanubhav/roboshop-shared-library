// Use this for container imaging

def call() {
    node {
        sh "rm -rf *"
        git branch: 'main', url: "https://github.com/b52-clouddevops/${COMPONENT}.git"
        common.lintChecks()


        stage('Docker Build') {
                
                sh "docker images "
        }

        if ( env.TAG_NAME != null ) {
            stage('Docker Push') {

                if (env.APP == "nodejs") {
                sh '''
                    npm install 
                    zip -r ${COMPONENT}-${TAG_NAME}.zip node_modules server.js schema
                  
                   '''
                }

                if (env.APP == "maven") {
                sh '''
                    mvn package 
                    cp target/${COMPONENT}-1.0.jar ${COMPONENT}.jar 
                    zip -r ${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar schema
                '''
                }

                if (env.APP == "python") {
                sh '''
                    zip -r ${COMPONENT}-${TAG_NAME}.zip *.ini *.py *.txt
                '''
                }

                if (env.APP == "nginx") {
                sh '''
                    zip -r ${COMPONENT}-${TAG_NAME}.zip *
                    zip -d ${COMPONENT}-${TAG_NAME}.zip Jenkinsfile 
                '''
                }
                        sh "docker build -t 834725375088.dkr.ecr.us-east-1.amazonaws.com/${COMPONENT}:latest ."
                        sh "docker tag 834725375088.dkr.ecr.us-east-1.amazonaws.com/${COMPONENT}:latest 834725375088.dkr.ecr.us-east-1.amazonaws.com/${COMPONENT}:${TAG_NAME}"
                        sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 834725375088.dkr.ecr.us-east-1.amazonaws.com"
                        sh "docker push 834725375088.dkr.ecr.us-east-1.amazonaws.com/${COMPONENT}:${TAG_NAME}"
                }
            }
        }
    }