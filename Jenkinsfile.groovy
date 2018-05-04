#!groovy
pipeline {
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
        stage('Deploy') {
            steps {
                bat "echo This is Deploy"
            }
        }
    }
}