pipeline {
  agent any
  tools {
        maven 'Maven3'
        jdk 'Java8'
  }
  stages {
    stage('Clean') {
      steps {
        sh 'mvn clean'
      }
    }
    stage('Compile') {
      steps {
        sh 'mvn compile'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
        junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
      }
    }
    stage('Package') {
      steps {
        sh 'mvn package'
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/CloudNet-Master.jar,**/target/CloudNet-Master.jar', fingerprint: true, onlyIfSuccessful: true      }
    }
  }
}
