def call(String agentLabel, String nodejsVersion) {
    pipeline {
        agent {
            label agentLabel
        }
        
        tools {
            nodejs nodejsVersion
        }

        stages {
            stage('Hello') {
                steps {
                    echo 'Hello World'
                }
            }
            stage('Check Versions') {
                steps {
                    sh 'node -v'
                    sh 'npm -v'
                    sh 'docker version'
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
