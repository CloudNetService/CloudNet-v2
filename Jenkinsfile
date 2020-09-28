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
        sh 'mvn versions:set -DnewVersion=2.2.0-SNAPSHOT'
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
    stage('Javadocs') {
        steps {
            sh 'mvn javadoc:aggregate-jar -P deployment'
        }
    }
    stage('Release ZIP') {
      steps {
        sh '''mkdir -p temp;
        cp -r .template/* temp/;
        cp LICENSE temp/;
        cp cloudnet-master/target/CloudNet-Master.jar temp/CloudNet-Master/;
        cp cloudnet-wrapper/target/CloudNet-Wrapper.jar temp/CloudNet-Wrapper/;
        '''
        zip archive: true, dir: 'temp', glob: '', zipFile: 'CloudNet.zip'
        sh 'rm -r temp/';
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/CloudNet-Wrapper.jar,**/target/CloudNet-Master.jar,**/target/CloudNetAPI.jar,target/cloudnet-*-javadoc.jar,cloudnet-tools/**/target/cloudnet-tools-*.jar', fingerprint: true, onlyIfSuccessful: true
      }
    }
    stage('Deploy') {
      when {
        anyOf {
          branch 'master'
          branch 'development'
        }
      }
      steps {
        withMaven(jdk: 'Java8', maven: 'Maven3', mavenSettingsConfig: '8bf610f1-24ed-48d5-8d4c-703b68cdb906', publisherStrategy: 'EXPLICIT', options: [dependenciesFingerprintPublisher(), artifactsPublisher(), mavenLinkerPublisher()]) {
          sh 'mvn -DskipTests -P deployment deploy'
        }
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
