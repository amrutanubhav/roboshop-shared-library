def call() {

    if (!env.TF_DIRECTORY) {

        env.TF_DIRECTORY = "./"

    }

    properties([
        parameters([
            choice(choices: 'dev\nprod', description: "Chose the Env", name: "ENV"),
            choice(choices: 'apply\ndestroy', description: "Choose apply or destroy", name: "ACTION")
        ])
    ])

    node {
            ansiColor('xterm'){
            sh "rm -rf *"   // removes all existing files in folders
            git branch: 'main', url: "https://github.com/amrutanubhav/${REPONAME}.git" // pulls the component from Git

        stage('Terraform Init') {
                sh '''
                        cd ${TF_DIRECTORY}
                        terrafile -f env-${ENV}/Terrafile
                        terraform init -backend-config=env-${ENV}/${ENV}-backend.tfvars
                '''

        }
        stage('Terraform Plan') {

                sh '''
                        cd ${TF_DIRECTORY}
                        terraform plan -var-file=env-${ENV}/${ENV}.tfvars
                    '''

        }
        stage('Terraform Action') {

                sh '''
                        cd ${TF_DIRECTORY}
                        terraform ${ACTION} -var-file=env-${ENV}/${ENV}.tfvars -auto-approve
                    '''

        }

            }
    }

}



// pipeline {
//     agent any 

//         parameters {
//              choice(name: 'ENV', choices: ['dev', 'prod'], description: 'Chose an environment')
//              choice(name: 'ACTION', choices: ['apply', 'destroy'], description: 'Chose action to be apply or destroy')
//         }
        
//         options {
//           ansiColor('xterm')
//     }

//     stages {

//         stage('Terraform Init') {
//             steps {
//                 sh "terrafile -f env-${ENV}/Terrafile"
//                 sh "terraform init -backend-config=env-${ENV}/${ENV}-backend.tfvars"
//             }
//         }

//         stage('Terraform Plan') {
//             steps {
//                 sh "terraform plan -var-file=env-${ENV}/${ENV}.tfvars"
//             }
//         }

//         stage('Terraform Action') {
//             steps {
//                 sh "terraform ${ACTION} -var-file=env-${ENV}/${ENV}.tfvars -auto-approve"
//             }
//         }
//     }
// }