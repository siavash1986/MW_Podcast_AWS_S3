#!/usr/bin/env groovy
pipeline{
    agent any

    stages{

        stage('build'){
            steps {
                sh 'echo Running build stage!'
                sh 'pwd'
                sh 'ls'
                sh 'gradle --version'
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