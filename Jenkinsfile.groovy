#!groovy
pipeline {
    environment{
        currentBuild.result = 'FAILURE'
    }
    agent any
    stages {
        stage('Build') {
            steps {
                bat "echo This is Build"
            }
        }
        stage('Test') {
            steps {
                bat "echo This is Test"
            }
        }
        stage('Experimental') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                bat "echo This is Experimental"
            }
        }
        stage('Deploy') {
            steps {
                bat "echo This is Deploy"
            }
        }
    }
}