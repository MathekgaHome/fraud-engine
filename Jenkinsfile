pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  environment {
    IMAGE_NAME      = 'fraud-service'
    IMAGE_TAG       = "${env.BUILD_NUMBER}"
    SPRING_PROFILE  = 'docker'
    COMPOSE_DIR     = '/opt/stack' // folder that contains docker-compose-all-services.yml under app root folder:  auth Jack
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'git log -1 --oneline'
      }
    }

    stage('Build') {
      steps {
        sh '''
          set -e
          java -version
          if [ -f mvnw ]; then
            chmod +x mvnw
            ./mvnw -B -DskipTests clean package -Dspring.profiles.active=${SPRING_PROFILE}
          else
            mvn -B -DskipTests clean package -Dspring.profiles.active=${SPRING_PROFILE}
          fi
        '''
      }
    }

    stage('Test') {
      steps {
        sh '''
          set -e
          if [ -f mvnw ]; then
            ./mvnw -B test -Dspring.profiles.active=${SPRING_PROFILE}
          else
            mvn -B test -Dspring.profiles.active=${SPRING_PROFILE}
          fi
        '''
      }
    }

    stage('Docker Build') {
      steps {
        sh '''
          set -e
          export DOCKER_BUILDKIT=0
          docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest .
        '''
      }
    }

    stage('Deploy') {
      when {
        anyOf {
          branch 'main'
        }
      }
      steps {
        sh '''
          set -e
          # Stop old instance and start updated container using correct service name
          docker compose -f ${COMPOSE_DIR}/docker-compose-all-services.yml up -d --force-recreate --no-deps fraud-service
          docker image prune -f
        '''
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
      archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: true
    }
    success {
      echo "OK — image ${IMAGE_NAME}:${IMAGE_TAG} (profile=${SPRING_PROFILE})"
    }
    failure {
      echo 'Build failed'
    }
  }
}