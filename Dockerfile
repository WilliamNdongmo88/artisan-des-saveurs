# Étape 1 : build du JAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : image finale avec Nginx + JAR
FROM nginx:1.25

# Installer Java (pour Spring Boot)
RUN apt-get update && apt-get install -y openjdk-17-jre && rm -rf /var/lib/apt/lists/*

# Dossier de l'application
WORKDIR /app

# Copier le jar
COPY --from=build /app/target/*.jar app.jar

# Copier la conf Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Script de démarrage : lancer Spring Boot + Nginx
COPY start.sh /start.sh
RUN chmod +x /start.sh

EXPOSE 8080

CMD ["/start.sh"]
