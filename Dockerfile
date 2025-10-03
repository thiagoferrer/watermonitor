FROM openjdk:17-jdk-slim  # ✅ Usar JDK 17 (compatível com seu setup)

WORKDIR /app

# Copia o JAR (nome correto)
COPY target/*.jar app.jar  # ✅ Pattern mais flexível

# Cria usuário não-root
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]