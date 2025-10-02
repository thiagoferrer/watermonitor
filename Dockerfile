# Dockerfile
FROM openjdk:21-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR (vamos construir ele no pipeline)
COPY target/monitor-0.0.1-SNAPSHOT.jar app.jar

# Cria um usuário não-root por segurança
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Expoe a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]