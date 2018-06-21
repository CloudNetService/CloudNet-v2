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
        sh 'mvn package javadoc:aggregate-jar'
      }
    }
    stage('Release ZIP') {
      steps {
        sh '''mkdir temp;
        cp -r .template/* temp/;
        cp cloudnet-core/target/CloudNet-Master.jar temp/CloudNet-Master/;
        cp cloudnet-wrapper/target/CloudNet-Wrapper.jar temp/CloudNet-Wrapper/;
        find cloudnet-tools/ -t file -name 'cloudnet-tools-*.jar' -exec cp '{}' temp/tools/ ';';
        zip CloudNet.zip 'temp/*';
        rm -r temp/''';
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/CloudNet-Wrapper.jar,**/target/CloudNet-Master.jar,**/target/CloudNetAPI.jar,target/cloudnet-*-javadoc.jar,cloudnet-tools/**/target/cloudnet-tools-*.jar,CloudNet.zip', fingerprint: true, onlyIfSuccessful: true
      }
    }
  }
}
