pipeline {
    agent any

    tools {
        maven 'M3.9.14'
        jdk 'JDK17'
    }

    environment {
        NEXUS_URL = 'http://10.0.128.227:8081'
        SONAR_URL = 'http://10.0.132.106:9000'
        
        / ✅ Ajout dockerhub credentials
        DOCKERHUB = credentials('dockerhub-credentials')

        // ✅ Le nom complet de ton repo DockerHub
        DOCKER_REPO = "belaididder/sa-backend-java-app"

    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'develop',
                    credentialsId: 'github-token',
                    url: 'https://github.com/idderbelaid/sa-backend.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
        /* ===================================================
           ✅ PARTIE DOCKER : Build, Tag, Login, Push
           =================================================== */

        stage('Docker Build') {
            steps {
                script {
                    sh "docker build -t sa-backend:latest ."
                }
            }
        }

        stage('Docker Tag') {
            steps {
                script {
                    sh "docker tag sa-backend:latest ${DOCKER_REPO}:latest"
                }
            }
        }

        stage('Docker Login') {
            steps {
                sh """
                    echo "${DOCKERHUB_PSW}" | docker login -u "${DOCKERHUB_USR}" --password-stdin
                """
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    sh "docker push ${DOCKER_REPO}:latest"
                }
            }
        }

        /* ===================================================
           ✅ Ensuite on pourra ajouter : Deploy Kubernetes
           =================================================== */
    }


    post {
        success {
            echo '✅ Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Pipeline échoué !'
        }
    }
}
