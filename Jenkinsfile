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
    stage('Package') {
      steps {
        sh 'mvn package'
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts artifacts: '**/target/*.jar', excludes: '**/target/original-*.jar'
      }
    }
  }
}
