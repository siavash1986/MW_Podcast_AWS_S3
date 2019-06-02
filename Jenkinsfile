#!/usr/bin/env groovy
@Library('jenkins-common@master')

pipeline{
    agent any

    tools {
        gradle "gradle5.0"
    }

    stages{

        stage('build'){
            steps {
                sh 'echo Running build stage!'
                sh 'pwd'
                sh 'ls'
                sh 'java -version'
                sh 'gradle --version'
                sh 'gradle clean'
                sh 'gradle build'
            }
        }


        stage('test'){
            steps {
                sh 'echo Running test stage!'
            }
        }

        stage('deploy'){
            steps {
                sh 'echo Running deploy stage1'
            }
        }

    }
}
