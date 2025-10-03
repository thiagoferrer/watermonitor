# Projeto - Cidades ESGInteligentes

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-yellow)

# Projeto - Cidades ESGInteligentes

Sistema de monitoramento de consumo de água para cidades inteligentes, desenvolvido como parte do projeto FIAP. A aplicação fornece uma API REST para gerenciar medições de consumo de água com alertas ESG.

## 📋 Como executar localmente com Docker

### Pré-requisitos
- Docker e Docker Compose instalados
- Java 21 (apenas para desenvolvimento)
- Maven (apenas para desenvolvimento)

### Execução com Docker Compose

1. **Clone o repositório:**
```bash
git clone <url-do-repositorio>
cd monitor
```

2. **Execute a aplicação:**
```bash
docker-compose up --build
```

3. **Acesse a aplicação:**
- API: http://localhost:8080/api/medicoes
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### Variáveis de ambiente para produção (Azure)
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-fiap.postgres.database.azure.com:5432/postgres
SPRING_DATASOURCE_USERNAME=fiap
SPRING_DATASOURCE_PASSWORD=SuaSenha123!
```

## 🔄 Pipeline CI/CD

### Ferramentas utilizadas
- **GitHub Actions** - Automação do pipeline
- **Docker** - Containerização da aplicação
- **Azure App Service** - Plataforma de deploy
- **PostgreSQL Azure** - Banco de dados em nuvem

### Etapas do Pipeline

1. **Checkout** - Obtém o código do repositório
2. **Setup Java** - Configura ambiente Java 21
3. **Build** - Compila o projeto com Maven
4. **Test** - Executa testes unitários
5. **Build Docker** - Constrói a imagem Docker
6. **Deploy to Azure** - Publica na Azure App Service

### Arquivo de configuração (.github/workflows/ci-cd.yml)
```yaml
name: Deploy to Azure App Service

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up Java
      uses: actions/setup-java@v2
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
    
    - name: Docker Build
      run: docker build -t monitor-app:${{ github.sha }} .
    
    - name: Deploy to Azure
      uses: azure/webapps-deploy@v2
      with:
        app-name: 'monitor-fiap'
        publish-profile: ${{ secrets.AZURE_WEBAPP_PUBLISH_PROFILE }}
        images: 'monitor-app:${{ github.sha }}'
```

## 🐳 Containerização

### Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR
COPY target/monitor-0.0.1-SNAPSHOT.jar app.jar

# Cria um usuário não-root por segurança
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Expoe a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Estratégias Adotadas

1. **Imagem Oficial** - Utiliza OpenJDK 21 slim para reduzir tamanho
2. **Segurança** - Cria usuário não-root para execução
3. **Multi-stage Build** - Separa ambiente de build e runtime
4. **Layer Caching** - Otimiza rebuilds mantendo dependências em camadas separadas

### Docker Compose para Desenvolvimento
```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/monitor_db
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=password123
    depends_on:
      - db

  db:
    image: postgres:13
    environment:
      - POSTGRES_DB=monitor_db
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password123
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
```

## 📸 Evidências de Funcionamento

### Endpoints da API

#### POST /api/medicoes
```json
{
  "localizacao": "Setor Comercial Norte",
  "consumoLitros": 1500.50,
  "dataMedicao": "2024-01-15",
  "alerta": "CONSUMO_ELEVADO"
}
```

#### GET /api/medicoes
Retorna lista de todas as medições cadastradas

#### GET /api/medicoes/{id}
Retorna uma medição específica

#### DELETE /api/medicoes/{id}
Remove uma medição

### Validações Implementadas
- ✅ Localização obrigatória
- ✅ Consumo deve ser positivo
- ✅ Data não pode ser futura
- ✅ Campo alerta obrigatório

### Funcionalidades de Segurança
- ✅ Configuração Spring Security
- ✅ CSRF desabilitado para APIs REST
- ✅ Swagger UI acessível

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 3.x** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Hibernate** - ORM
- **Maven** - Gerenciamento de dependências

### Banco de Dados
- **PostgreSQL 13** - Banco de dados principal
- **H2 Database** - Banco em memória para testes

### Ferramentas de Desenvolvimento
- **Docker & Docker Compose** - Containerização
- **SpringDoc OpenAPI 3** - Documentação da API (Swagger)
- **GitHub Actions** - CI/CD
- **DBeaver** - Cliente de banco de dados

### Infraestrutura
- **Azure App Service** - Deploy em produção
- **Azure PostgreSQL** - Banco de dados em nuvem
- **GitHub** - Versionamento e CI/CD

### Monitoramento e Qualidade
- **Swagger UI** - Documentação interativa da API
- **Global Exception Handler** - Tratamento centralizado de erros
- **Bean Validation** - Validação de dados de entrada

## 📊 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/monitor/
│   │   ├── controller/     # Controladores REST
│   │   ├── service/        # Lógica de negócio
│   │   ├── repository/     # Camada de dados
│   │   ├── model/          # Entidades JPA
│   │   ├── security/       # Configurações de segurança
│   │   └── exception/      # Tratamento de exceções
│   └── resources/
│       └── application.properties
```

A aplicação está configurada para ambiente de produção na Azure com variáveis de ambiente seguras e deploy automatizado através do pipeline CI/CD.

## 👥 Desenvolvido por

**Equipe Cidades ESGInteligentes**  
*Tecnologia para Cidades Sustentáveis*
```
Guilherme Fernandes - RM558174
Cauã Rodrigues - RM557062
Gustavo Godoy - RM556757
Thiago Carvalho - RM554460
```
---

**📄 Licença:** MIT  
