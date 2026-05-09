pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'hospital-management'
        DOCKER_TAG = "${BUILD_NUMBER}"
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-amd64"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
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
                    sh 'java -version'
                    sh 'mvn -version'
                    sh 'mvn clean compile -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                dir('backend/hospital-management') {
                    sh 'mvn test -DskipTests'
                }
            }
        }

        stage('Package') {
            steps {
                dir('backend/hospital-management') {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('Docker Build') {
            steps {
                dir('backend/hospital-management') {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
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
