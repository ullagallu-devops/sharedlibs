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
        stages {
            stage('NPM Dependency Audit') {
                steps {
                    sh '''
                    npm audit --audit-level=critical'
                    echo $?
                    '''
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
