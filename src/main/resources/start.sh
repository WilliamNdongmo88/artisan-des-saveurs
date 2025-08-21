#!/bin/bash
# Lancer Spring Boot en arrière-plan sur 8081
java -jar /app/app.jar --server.port=8081 &

# Lancer Nginx au premier plan (Railway attend un process en foreground)
nginx -g "daemon off;"
