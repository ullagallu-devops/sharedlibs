def call(String agentLabel, String nodejsVersion) {
    pipeline {
        agent {
            label agentLabel
        }
        
        tools {
            nodejs nodejsVersion
        }

        stages {
            stage('Debug Branch Name') {
                steps {
                    script {
                        echo "Detected Branch Name: ${env.BRANCH_NAME}"
                        echo "Detected Git Branch: ${env.GIT_BRANCH}"
                    }
                }
            }
            stage('Install Dependencies') {
                when { expression { env.BRANCH_NAME?.startsWith('feature/') || env.GIT_BRANCH?.startsWith('feature/') } }
                steps {
                    sh 'npm install --no-audit'
                }
            }
            stage("Dependency Scanning Parallel"){
                when { expression { env.BRANCH_NAME?.startsWith('feature/') || env.GIT_BRANCH?.startsWith('feature/') } }
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
                                --prettyPrint''', odcInstallation: 'OWASP-DP-10'

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
