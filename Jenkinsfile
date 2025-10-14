pipeline {
    agent any

    tools {
        // Si tu utilises Maven ou Gradle
        maven 'Maven 3.8.1' // ou le nom configuré dans Jenkins
    }

    stages {
        stage('Cloner le projet') {
            steps {
                git 'https://github.com/ton-utilisateur/ton-projet.git'//update url of git repoistory
            }
        }

        stage('Build') {
            steps {
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