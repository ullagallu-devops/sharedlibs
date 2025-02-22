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
            stage("Dependency Scanning Parallel"){
                 parallel{
                    stage('NPM Dependency Audit') {
                        steps {
                            sh '''
                            npm audit --audit-level=critical
                            echo $?
                            '''
                        }
                    }
                    stage('OWASP Dependency Check') {
                            steps {
                                dependencyCheck additionalArguments: '''
                                    --scan \'./\' 
                                    --out \'./\'  
                                    --format \'ALL\' 
                                    --disableYarnAudit \
                                    --prettyPrint''', odcInstallation: 'OWASP-DP-10'
                            }
                    }
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
