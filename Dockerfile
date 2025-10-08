# ===========================
# 🏗️ Build stage
# ===========================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia pom.xml e dependências primeiro (cache eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e empacota
COPY src ./src
RUN mvn clean package -DskipTests

# ===========================
# 🚀 Runtime stage
# ===========================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Instala curl (para healthcheck)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Cria usuário não-root
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Define variáveis de ambiente
ARG PORT=8080
ENV SERVER_PORT=$PORT
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

# E
