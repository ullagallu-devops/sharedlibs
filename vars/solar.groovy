def call(String agentLabel, String nodejsVersion) {
    pipeline {
        agent {
            label agentLabel
        }

        environment {
            NVD_API_KEY = credentials('NVD_API_KEY')
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
                parallel {
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
                                --prettyPrint \
                                --nvdApiKey ${NVD_API_KEY} \
                                --nvdApiDelay 5000''', odcInstallation: 'OWASP-DP-10'
                            dependencyCheckPublisher failedTotalCritical: 1, pattern: 'dependency-check-report.xml', stopBuild: false
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
