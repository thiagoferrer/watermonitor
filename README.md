# Projeto Monitor - Sistema de Monitoramento

## 📋 Sobre o Projeto
Sistema de monitoramento e gestão de recursos desenvolvido em Spring Boot com arquitetura containerizada e pipeline CI/CD completo.

## 🚀 Como Executar Localmente com Docker

### Pré-requisitos
- Docker e Docker Compose instalados
- Java 17 ou superior (para desenvolvimento)

### Execução com Docker Compose

1. **Clone o projeto:**
```bash
git clone <url-do-repositorio>
cd monitor
```

2. **Execute o ambiente completo:**
```bash
docker-compose up -d
```

3. **Acesse a aplicação:**
- API: http://localhost:8080
- H2 Console (dev): http://localhost:8080/h2-console
- Swagger UI: http://localhost:8080/swagger-ui.html

### Variáveis de Ambiente
Crie um arquivo `.env`:
```env
DB_USER=postgres
DB_PASSWORD=password123
SPRING_PROFILES_ACTIVE=docker
```

## 🔄 Pipeline CI/CD

### Ferramentas Utilizadas
- **GitHub Actions** para automação do pipeline
- **Docker** para containerização
- **Docker Compose** para orquestração
- **GitHub Container Registry** para registro de imagens

### Etapas do Pipeline

1. **Build e Testes**
    - Checkout do código
    - Setup Java 17
    - Build com Maven
    - Execução de testes unitários

2. **Build de Imagem Docker**
    - Construção da imagem da aplicação
    - Push para GitHub Container Registry

3. **Deploy Staging**
    - Deploy automático para ambiente de staging
    - Validação com banco PostgreSQL

4. **Deploy Produção**
    - Deploy manual para produção
    - Configurações otimizadas para produção

### Arquivo do Pipeline (.github/workflows/ci-cd.yml)
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: mvn clean package -DskipTests
      - run: mvn test

  build-docker-image:
    needs: build-and-test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build Docker image
        run: docker build -t ghcr.io/your-username/monitor-app:latest .
      - name: Push to GitHub Container Registry
        run: |
          echo "${{ secrets.GITHUB_TOKEN }}" | docker login ghcr.io -u ${{ github.actor }} --password-stdin
          docker push ghcr.io/your-username/monitor-app:latest
```

## 🐳 Containerização

### Dockerfile
```dockerfile
# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create non-root user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

### Estratégias Adotadas
- **Multi-stage build** para reduzir tamanho da imagem final
- **Usuário não-root** para segurança
- **Health checks** para monitoramento
- **Otimizações JVM** para containers
- **Logs persistidos** em volumes

### Docker Compose
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: monitor_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - monitor-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/monitor_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password123
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - monitor-network

networks:
  monitor-network:
    driver: bridge

volumes:
  postgres_data:
```

## 📸 Evidências de Funcionamento

### API Endpoints
- `POST /api/medicoes` - Criar medição
- `GET /api/medicoes` - Listar todas as medições
- `GET /api/medicoes/{id}` - Buscar medição por ID
- `DELETE /api/medicoes/{id}` - Excluir medição

### Exemplo de Uso
```bash
# Criar medição
curl -X POST http://localhost:8080/api/medicoes \
  -H "Content-Type: application/json" \
  -d '{
    "localizacao": "Setor A",
    "consumoLitros": 1500.5,
    "dataMedicao": "2024-01-15",
    "alerta": "NORMAL"
  }'

# Listar medições
curl http://localhost:8080/api/medicoes
```

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.6** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Spring Validation** - Validação de dados
- **SpringDoc OpenAPI** - Documentação da API

### Banco de Dados
- **PostgreSQL** - Banco principal (produção)
- **H2** - Banco em memória (desenvolvimento)

### Containerização e DevOps
- **Docker** - Containerização
- **Docker Compose** - Orquestração
- **GitHub Actions** - CI/CD
- **GitHub Container Registry** - Registry de imagens

### Ferramentas de Desenvolvimento
- **Maven** - Gerenciamento de dependências
- **Spring Boot DevTools** - Desenvolvimento
- **Spring Boot Actuator** - Monitoramento

### Monitoramento e Documentação
- **Spring Boot Actuator** - Health checks e métricas
- **Swagger/OpenAPI** - Documentação interativa
- **H2 Console** - Interface do banco em dev

---

## ✅ Checklist de Entrega

| Item | Status |
|------|--------|
| Projeto compactado em .ZIP com estrutura organizada | ✅ |
| Dockerfile funcional | ✅ |
| docker-compose.yml ou arquivos Kubernetes | ✅ |
| Pipeline com etapas de build, teste e deploy | ✅ |
| README.md com instruções e prints | ✅ |
| Documentação técnica com evidências (PDF ou PPT) | ✅ |
| Deploy realizado nos ambientes staging e produção | ✅ |

---

**Equipe:** [Nomes dos integrantes]  
**Data de Entrega:** [Data]  
**Disciplina:** Cidades ESGInteligentes