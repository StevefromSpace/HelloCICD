pipeline {
    agent any
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/StevefromSpace/HelloCICD.git'
            }
        }
        stage('Maven Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Automated Testing') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Package Application') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t hellocicd .'
            }
        }
        stage('Deployment') {
            steps {
                sh 'docker rm -f hellocicd-container || true'
                sh 'docker run -d -p 8085:8085 --name hellocicd-container hellocicd'
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo 'CI/CD Pipeline Completed Successfully!'
        }
        failure {
            echo 'CI/CD Pipeline Failed. Check Console Output.'
        }
    }
}