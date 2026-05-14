pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'preethamvs6/hospital-management'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
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

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker push ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Detect if using 'docker compose' or 'docker-compose'
                    def dockerComposeCmd = sh(script: 'docker compose version > /dev/null 2>&1 && echo "docker compose" || echo "docker-compose"', returnStdout: true).trim()
                    echo "Using command: ${dockerComposeCmd}"
                    
                    sh "${dockerComposeCmd} down || true"
                    sh "${dockerComposeCmd} up -d"
                }
            }
        }

        stage('Health Check') {
            steps {
                echo 'Waiting for application to start...'
                sleep 20
                // Simple check if the container is running
                sh 'docker ps | grep hospital-app'
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
