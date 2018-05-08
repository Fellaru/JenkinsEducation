#!groovy
pipeline {
    agent any
    environment {
        FOO = "BAR"
        BUILD_NUM_ENV = currentBuild.getNumber()
        ANOTHER_ENV = "${currentBuild.getNumber()}"
        INHERITED_ENV = "\${BUILD_NUM_ENV} is inherited"
    }
    stages {
        stage('Build') {
            steps {
                bat "echo This is Build"
                script {
                    currentBuild.result = 'SUCCESS'
                }
            }
        }
        stage('Test') {
            steps {
                bat "echo This is Test"
            }
        }
        stage('Experimental') {
            steps {
                bat 'echo "FOO is %FOO%"'
                // returns 'FOO is BAR'

                bat 'echo "BUILD_NUM_ENV is $BUILD_NUM_ENV"'
                // returns 'BUILD_NUM_ENV is 4' depending on the build number

                bat 'echo "ANOTHER_ENV is $ANOTHER_ENV"'
                // returns 'ANOTHER_ENV is 4' like the previous depending on the build number

                bat 'echo "INHERITED_ENV is $INHERITED_ENV"'
                // returns 'INHERITED_ENV is ${BUILD_NUM_ENV} is inherited'
                // The \ escapes the $ so the variable is not expanded but becomes a literal
            }
        }
        stage('Deploy') {
            steps {
                bat "echo This is Deploy"
            }
        }
    }
}