# Estágio de construção (Build)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia os arquivos do projeto
COPY pom.xml .
COPY src ./src

# Compila o projeto (ignora testes por enquanto)
RUN mvn clean package -DskipTests

# Estágio de execução (Runtime)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o JAR gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

# Variáveis de ambiente
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]