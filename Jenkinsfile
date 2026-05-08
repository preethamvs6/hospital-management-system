pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'hospital-management'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                dir('backend/hospital-management') {
                    bat 'mvn clean compile -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                dir('backend/hospital-management') {
                    bat 'mvn test'
                }
            }
        }

        stage('Package') {
            steps {
                dir('backend/hospital-management') {
                    bat 'mvn package -DskipTests'
                }
            }
        }

        stage('Docker Build') {
            steps {
                dir('backend/hospital-management') {
                    bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    bat "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                bat 'docker-compose down || true'
                bat 'docker-compose up -d'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs.'
        }
        always {
            cleanWs()
        }
    }
}
