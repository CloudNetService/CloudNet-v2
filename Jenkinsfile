pipeline {
  agent any
  tools {
        maven 'Maven3'
        jdk 'Java8'
  }
  options {
    buildDiscarder logRotator(numToKeepStr: '10')
  }
  stages {
    stage('Clean') {
      steps {
        sh 'mvn clean'
      }
    }
    stage('Version') {
      steps {
        sh 'mvn versions:set -DnewVersion=2.2.0'
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
    stage('Release ZIP') {
      steps {
        sh '''mkdir -p temp;
        cp -r .template/* temp/;
        cp cloudnet-core/target/CloudNet-Master.jar temp/CloudNet-Master/;
        cp cloudnet-wrapper/target/CloudNet-Wrapper.jar temp/CloudNet-Wrapper/;
        '''
        zip archive: true, dir: 'temp', glob: '', zipFile: 'CloudNet.zip'
        sh 'rm -r temp/';
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/CloudNet-Wrapper.jar,**/target/CloudNet-Master.jar,**/target/CloudNetAPI.jar,target/cloudnet-*-javadoc.jar', fingerprint: true, onlyIfSuccessful: true
      }
    }
  }
  post {
    always {
      withCredentials([string(credentialsId: 'cloudnet-discord-ci-webhook', variable: 'url')]) {
        discordSend description: 'New build for CloudNet!', footer: 'New build!', link: env.BUILD_URL, successful: currentBuild.resultIsBetterOrEqualTo('SUCCESS'), title: JOB_NAME, webhookURL: url
      }
    }
  }
}
