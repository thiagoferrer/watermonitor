# 🌱 Projeto Monitor - Sistema de Gestão ESG

## 📋 Descrição do Projeto
Sistema de monitoramento e gestão de recursos para cidades inteligentes, com foco em métricas ESG (Environmental, Social, and Governance). A aplicação permite o cadastro, consulta e análise de medições ambientais, facilitando a tomada de decisão sustentável.

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.6** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Spring Validation** - Validação de dados
- **SpringDoc OpenAPI** - Documentação da API

### Banco de Dados
- **PostgreSQL 13** - Banco de dados principal
- **H2 Database** - Banco em memória para desenvolvimento

### Infraestrutura e DevOps
- **Docker** - Containerização
- **Docker Compose** - Orquestração de containers
- **GitHub Actions** - CI/CD Pipeline
- **Maven** - Gerenciamento de dependências

### Monitoramento e Documentação
- **Spring Boot Actuator** - Health checks e métricas
- **Swagger UI** - Documentação interativa da API


## 📁 Estrutura do Projeto
```
monitor/
├── .github/workflows/
│   └── ci-cd.yml                 # Pipeline CI/CD
├── src/main/java/com/monitor/
│   ├── controller/               # Controladores REST
│   ├── service/                  # Lógica de negócio
│   ├── repository/               # Camada de dados
│   ├── model/                    # Entidades JPA
│   ├── exception/               # Tratamento de exceções
│   └── security/                # Configuração de segurança
├── src/main/resources/
│   ├── application.properties    # Configuração principal
│   ├── application-dev.properties # Config desenvolvimento
│   ├── application-docker.properties # Config Docker
│   └── application-production.properties # Config produção
├── Dockerfile                   # Definição da imagem Docker
├── docker-compose.yml           # Orquestração desenvolvimento
├── docker-compose.production.yml # Orquestração produção
└── Docker-Compose-Staging.yml   # Orquestração staging
```
## 🏃‍♂️ Como Executar Localmente

### Pré-requisitos
- Java 17
- Maven 3.9+
- Docker e Docker Compose

### Opção 1: Execução com Docker Compose (Recomendada)

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd monitor

# Execute com Docker Compose
docker-compose up -d

# A aplicação estará disponível em: http://localhost:8080
```

### Opção 2: Execução com Maven

```bash
# Build do projeto
mvn clean package

# Execução com perfil de desenvolvimento
java -jar target/monitor-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# A aplicação estará disponível em: http://localhost:8082
```

## 🐳 Containerização

### Dockerfile
```dockerfile
# Estágio de build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio de runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY --from=build /app/target/*.jar app.jar
RUN groupadd -r spring && useradd -r -g spring spring
USER spring
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Serviços Docker Compose
- **Aplicação**: Spring Boot na porta 8080
- **Banco de Dados**: PostgreSQL na porta 5432
- **Rede**: Rede bridge dedicada
- **Volumes**: Persistência de dados

## 🔄 Pipeline CI/CD

### Estrutura do Pipeline
```yaml
name: CI/CD Pipeline
on: [push, pull_request]
jobs:
  build-and-test:    # Build e testes
  build-docker-image: # Build da imagem Docker
  deploy-staging:    # Deploy em staging
  deploy-production: # Deploy em produção
```

### Etapas do Pipeline
1. **Build e Testes**
    - Checkout do código
    - Setup Java 17
    - Build com Maven
    - Execução de testes com PostgreSQL

2. **Build da Imagem Docker**
    - Login no Docker Hub
    - Build multi-stage
    - Push da imagem com tags

3. **Deploy Staging** (branch develop)
    - Deploy automático
    - Health checks
    - Rollback automático em falhas

4. **Deploy Produção** (branch main)
    - Deploy com aprovação
    - Configurações de produção
    - Monitoramento ativo

## 📊 API Endpoints

### Medições
- `POST /api/medicoes` - Criar medição
- `GET /api/medicoes` - Listar todas as medições
- `GET /api/medicoes/{id}` - Buscar medição por ID
- `DELETE /api/medicoes/{id}` - Excluir medição

### Health Check
- `GET /api/health` - Status da aplicação
- `GET /actuator/health` - Health check detalhado

### Documentação
- `GET /swagger-ui.html` - Interface Swagger UI
- `GET /api-docs` - Especificação OpenAPI

## 🔧 Configurações por Ambiente

### Desenvolvimento (dev)
- Banco H2 em memória
- DDL auto: create-drop
- Logging detalhado

### Docker
- PostgreSQL em container
- DDL auto: update
- Health checks

### Produção
- PostgreSQL com variáveis de ambiente
- DDL auto: validate
- Logging otimizado

## 🛡️ Segurança

- Spring Security configurado
- CSRF desabilitado para API REST
- Headers de segurança
- Configuração CORS

## 📈 Monitoramento

- Spring Boot Actuator
- Health checks personalizados
- Métricas de aplicação
- Logs estruturados

## 👥 Equipe de Desenvolvimento

- **Guilherme Fernandes** - RM558174
- **Cauã Rodrigues** - RM557062
- **Gustavo Godoy** - RM556757
- **Thiago Carvalho** - RM554460

📋 Checklist de Entrega

| Item | Status |
|------|--------|
| Projeto compactado em .ZIP com estrutura organizada | ✅ |
| Dockerfile funcional | ✅ |
| docker-compose.yml ou arquivos Kubernetes | ✅ |
| Pipeline com etapas de build, teste e deploy | ✅ |
| README.md com instruções e prints | ✅ |
| Documentação técnica com evidências (PDF ou PPT) | ✅ |
| Deploy realizado nos ambientes staging e produção | ✅ |

```
**🎓 Disciplina:** Navegando pelo Mundo DevOps  
**📅 Data de Entrega:** Outubro/2025
```
