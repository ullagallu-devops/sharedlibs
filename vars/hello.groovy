def call(String agentLabel) {
    pipeline {
        agent {
            label agentLabel
        }

        stages {
            stage('Hello') {
                steps {
                    echo 'Hello World'
                }
            }
            stage('docker version') {
                steps {
                    sh "docker version"
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
