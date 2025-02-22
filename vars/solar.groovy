def call(String agentLabel, String nodejsVersion) {
    pipeline {
        agent {
            label agentLabel
        }
        
        tools {
            nodejs nodejsVersion
        }

        stages {
            stage('Install Dependencies') {
                steps {
                    sh 'npm install --no-audit'
                }
            }
        }
        post {
            always {
                cleanWs()
            }
        }
    }
}
