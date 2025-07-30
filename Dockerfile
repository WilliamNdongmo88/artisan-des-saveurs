# Étape 1 : Build avec Maven
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Étape 1.1 - Copier uniquement le pom.xml pour pré-télécharger les dépendances
COPY pom.xml .

# Télécharge les dépendances pour éviter les problèmes de compilation
RUN mvn dependency:go-offline -B

# Étape 1.2 - Copier le code source après les dépendances
COPY src ./src

# Compiler l'application
RUN mvn clean package -DskipTests

# Étape 2 : Exécution avec une image Java légère
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port de l'application
EXPOSE 8080

# Affiche les variables utiles et démarre l'application
ENTRYPOINT ["sh", "-c", "echo MAIL_USERNAME=$MAIL_USERNAME && java -jar app.jar"]
