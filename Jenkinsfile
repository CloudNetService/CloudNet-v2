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
    stage('Re-package') {
      steps {
        sh 'mvn -Xdoclint:none package javadoc:aggregate'
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/CloudNet-Wrapper.jar,**/target/CloudNet-Master.jar,**/target/CloudNetAPI.jar,target/cloudnet-*-javadoc.jar', fingerprint: true, onlyIfSuccessful: true
      }
    }
  }
}
