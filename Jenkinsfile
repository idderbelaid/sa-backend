pipeline {
    agent any

    tools {
        // Si tu utilises Maven ou Gradle
        maven 'Maven 3.8.1' // ou le nom configuré dans Jenkins
    }

    stages {
        stage('Cloner le projet') {
            steps {
                git branch: 'main', url:  'https://github.com/idderbelaid/sa-backend.git'//update url of git repository
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package' // ou './gradlew build' si tu utilises Gradle
            }
        }

        stage('Tests') {
            steps {
                sh './mvnw test' // ou './gradlew test'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }
}