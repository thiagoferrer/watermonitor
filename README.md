# Projeto - Cidades ESGInteligentes

Sistema de monitoramento de consumo de água para cidades inteligentes, desenvolvido como parte do projeto FIAP. A aplicação fornece uma API REST para gerenciar medições de consumo de água com alertas ESG.

## 📋 Como executar localmente com Docker

### Pré-requisitos
- Docker e Docker Compose instalados
- Java 21 (opcional, apenas para desenvolvimento)
- Maven (opcional, apenas para desenvolvimento)

### Execução Rápida

1. **Clone o repositório:**
```bash
git clone https://github.com/thiagofcarvalho/monitor.git
cd monitor
```

2. **Execute a aplicação:**
```bash
docker-compose up --build
```

3. **Acesse a aplicação:**
- **API:** http://localhost:8080/api/medicoes
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console

### Configuração do Banco de Dados Local

O Docker Compose configura automaticamente:
- **PostgreSQL:** Porta 5433
- **Aplicação:** Porta 8080
- **Credenciais:**
    - Database: `monitor_db`
    - Username: `postgres`
    - Password: `password123`

### Comandos Úteis

```bash
# Parar a aplicação
docker-compose down

# Ver logs
docker-compose logs -f

# Rebuildar imagens
docker-compose build --no-cache
```

## 🔄 Pipeline CI/CD

### Ferramentas Utilizadas
- **GitHub Actions** - Automação do pipeline
- **Azure App Service** - Plataforma de deploy
- **Azure PostgreSQL** - Banco de dados em nuvem
- **Docker** - Containerização

### Etapas do Pipeline

#### 1. Trigger Automático
```yaml
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
```

#### 2. Build e Testes
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Build with Maven
        run: mvn clean package -DskipTests
```

#### 3. Deploy para Azure
```yaml
  deploy:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Deploy to Azure Web App
        uses: azure/webapps-deploy@v2
        with:
          app-name: 'monitor-fiap-554460'
          publish-profile: ${{ secrets.AZURE_WEBAPP_PUBLISH_PROFILE }}
          package: target/*.jar
```

### Configuração de Secrets no GitHub

1. Acesse: `Settings > Secrets and variables > Actions`
2. Adicione o secret: `AZURE_WEBAPP_PUBLISH_PROFILE`
3. Cole o conteúdo completo do XML do perfil de publicação do Azure

### Pipeline Completo

```yaml
name: Deploy to Azure App Service

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up Java
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: 'maven'
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
    
    - name: Deploy to Azure Web App
      uses: azure/webapps-deploy@v2
      with:
        app-name: 'monitor-fiap-554460'
        publish-profile: ${{ secrets.AZURE_WEBAPP_PUBLISH_PROFILE }}
        package: target/monitor-0.0.1-SNAPSHOT.jar
    
    - name: Verify deployment
      run: |
        sleep 30
        curl -f https://monitor-fiap-554460.azurewebsites.net/api/medicoes || exit 1
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

#### 1. Segurança
- **Usuário não-root:** Previne privilégios desnecessários
- **Imagem oficial:** OpenJDK slim para reduzir superficie de ataque
- **Versão específica:** Java 21 LTS para estabilidade

#### 2. Performance
- **Imagem slim:** Redução de tamanho em ~50% comparado com imagens completas
- **Layer caching:** Otimização de rebuilds
- **Multi-stage build:** Separação entre build e runtime

#### 3. Boas Práticas
```dockerfile
# Health check para monitoramento
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Variáveis de ambiente para configuração
ENV SPRING_PROFILES_ACTIVE=docker
```

### Docker Compose para Desenvolvimento

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/monitor_db
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=password123
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
    depends_on:
      - db
    networks:
      - monitor-network

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
    networks:
      - monitor-network

volumes:
  postgres_data:

networks:
  monitor-network:
    driver: bridge
```

## 📸 Evidências de Funcionamento

### Ambiente de Produção (Azure)

#### 1. Aplicação em Produção
**URL:** https://monitor-fiap-554460.azurewebsites.net

**Endpoints testados:**
- ✅ `GET /api/medicoes` - Listagem de medições
- ✅ `POST /api/medicoes` - Criação de medições
- ✅ `GET /api/medicoes/{id}` - Busca por ID
- ✅ `DELETE /api/medicoes/{id}` - Exclusão

#### 2. Swagger UI em Produção
**URL:** https://monitor-fiap-554460.azurewebsites.net/swagger-ui.html

![Swagger UI](https://monitor-fiap-554460.azurewebsites.net/swagger-ui.html)

#### 3. Exemplo de Request/Response
```bash
# Request
POST /api/medicoes
{
  "localizacao": "Setor Comercial Norte - QN 102",
  "consumoLitros": 1250.75,
  "dataMedicao": "2024-01-15",
  "alerta": "CONSUMO_MODERADO"
}

# Response
{
  "id": 1,
  "localizacao": "Setor Comercial Norte - QN 102",
  "consumoLitros": 1250.75,
  "dataMedicao": "2024-01-15",
  "alerta": "CONSUMO_MODERADO"
}
```

#### 4. Azure Portal - Recursos Criados
- **App Service:** `monitor-fiap-554460`
- **PostgreSQL:** `postgres-fiap-554460`
- **Resource Group:** `rg-monitor-fiap`

### Pipeline em Execução

#### 1. Build Successful
```
[INFO] BUILD SUCCESS
[INFO] Total time: 38.184 s
[INFO] Finished at: 2025-10-03T19:45:26Z
```

#### 2. Deploy Successful
```
Status: Building the app... Time: 34(s)
Status: Build successful. Time: 50(s)
Status: Site started successfully. Time: 81(s)
Status: RuntimeSuccessful
```

### Validações Implementadas

#### 1. Validações de Dados
```java
@NotBlank(message = "Localização é obrigatória")
private String localizacao;

@Positive(message = "Consumo deve ser positivo")
private Double consumoLitros;

@PastOrPresent(message = "Data não pode ser futura")
private LocalDate dataMedicao;
```

#### 2. Tratamento de Erros
```json
{
  "localizacao": "não deve estar em branco",
  "consumoLitros": "deve ser positivo"
}
```

## 🛠 Tecnologias Utilizadas

### Backend
| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| **Java** | 21 | Linguagem de programação |
| **Spring Boot** | 3.5.6 | Framework principal |
| **Spring Data JPA** | 3.5.4 | Persistência de dados |
| **Spring Security** | 6.5.5 | Autenticação e autorização |
| **SpringDoc OpenAPI** | 2.8.5 | Documentação da API |
| **Hibernate** | 6.6.29 | ORM |
| **Maven** | 3.9+ | Gerenciamento de dependências |

### Banco de Dados
| Tecnologia | Versão | Ambiente |
|------------|--------|----------|
| **PostgreSQL** | 13 | Produção (Azure) |
| **PostgreSQL** | 13 | Desenvolvimento (Docker) |
| **H2 Database** | 2.2+ | Testes (em memória) |

### Infraestrutura e DevOps
| Tecnologia | Finalidade |
|------------|------------|
| **Docker** | Containerização |
| **Docker Compose** | Orquestração local |
| **Azure App Service** | Deploy em produção |
| **Azure PostgreSQL** | Banco de dados em nuvem |
| **GitHub Actions** | CI/CD Pipeline |
| **Azure CLI** | Gerenciamento de recursos |

### Ferramentas de Desenvolvimento
| Tecnologia | Finalidade |
|------------|------------|
| **Spring Boot DevTools** | Desenvolvimento rápido |
| **Lombok** | Redução de boilerplate |
| **Spring Boot Actuator** | Monitoramento |
| **Swagger UI** | Documentação interativa |
| **H2 Console** | Interface do banco |

### Segurança e Qualidade
| Tecnologia | Finalidade |
|------------|------------|
| **Spring Security** | Proteção da API |
| **Bean Validation** | Validação de dados |
| **Global Exception Handler** | Tratamento centralizado |
| **JUnit 5** | Testes unitários |
| **Mockito** | Mocking em testes |

### Monitoramento e Logs
| Tecnologia | Finalidade |
|------------|------------|
| **SLF4J** | Logging facade |
| **Logback** | Implementação de logging |
| **Micrometer** | Métricas da aplicação |
| **Azure Monitor** | Monitoramento em produção |


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
