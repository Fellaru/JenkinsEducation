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
                writeFile text: 'hello world', file: 'msg.txt'
                step([$class: 'Mailer', recipients: 'efischenko@cinimex.ru'])
            }
        }
        stage('Deploy') {
            steps {
                bat "echo This is Deploy"
            }
        }
    }
}