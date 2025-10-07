
# Utilise une image officielle et sécurisée de Java 17 maintenue par Adoptium
FROM eclipse-temurin:17-jdk


# Définit le répertoire de travail dans le conteneur
WORKDIR /app

# Copie le fichier JAR généré dans le conteneur
COPY target/sa-backend-0.0.1-SNAPSHOT.jar sa-backend.jar

# Expose le port utilisé par Spring Boot (par défaut 8080)
EXPOSE 8080

# Commande exécutée au démarrage du conteneur
ENTRYPOINT ["java", "-jar", "sa-backend.jar"]
