# Sistema de Votação em Assembleias

![Coverage](https://img.shields.io/badge/coverage-93%25-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![Docker](https://img.shields.io/badge/Docker-enabled-blue)

Sistema para gerenciamento de sessões de votação em assembleias, permitindo cadastro de pautas, abertura de sessões de
votação e contabilização de votos.

## 📋 Pré-requisitos

- Java JDK 17
- Docker e Docker Compose
- Gradle 8.5+

## 🚀 Começando

### Clonando o repositório

```bash
  git clone git@github.com:camilabarpp/votacao-desafio.git
```

### Configurando o ambiente

1. **Iniciando o banco de dados com Docker:**
   ```bash
   docker-compose up -d
   ```
2. **Buildando o projeto:**
   ```bash
   ./gradlew clean build
   ```
3. **Iniciando a aplicação:**
   ```bash
   ./gradlew bootRun
   ```

A aplicação estará disponível em: `http://localhost:8080/api/v1/votacao`

## 📖 Documentação da API

A documentação completa da API está disponível através do Swagger UI:
- URL: `http://localhost:8080/api/v1/votacao/swagger-ui/index.html#/`
- Collection: Arquivo com todas as requisições: `Votação- Desafio.postman_collection.json`

## 🔍 Endpoints Principais

- `POST /api/v1/votacao/pautas` - Criar nova pauta
- `POST /api/v1/votacao/pauta/{pautaId}/sessao` - Abrir sessão de votação
- `POST /votos/pauta/{{pautaId}}` - Registrar voto
- `GET /pautas/{pautaId}/resultado` - Consultar resultado da votação

## 🧪 Testes

### Executando testes unitários
```bash
  ./gradlew test
```

### Verificando cobertura de testes
```bash
  ./gradlew test jacocoTestReport
```

O relatório de cobertura estará disponível em: `build/reports/jacoco/index.html`

### Verificando cobertura mínima
```bash
  ./gradlew test jacocoTestCoverageVerification
```

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas:
- **Controller**: APIs REST
- **Service**: Regras de negócio
- **Repository**: Camada de persistência
- **Entity**: Modelos de dados
- **DTO**: Objetos de transferência de dados

## 🔧 Stack Tecnológica

- **Spring Boot**: Framework principal
- **Spring Data JPA**: Persistência de dados
- **PostgreSQL**: Banco de dados
- **Flyway**: Migrations do banco
- **Swagger/OpenAPI**: Documentação da API
- **JUnit 5**: Framework de testes
- **Mockito**: Framework para mocks em testes
- **JaCoCo**: Cobertura de testes
- **Lombok**: Redução de boilerplate
- **Docker**: Containerização

## 📊 Métricas de Qualidade

- Cobertura de testes: 93%
- Cobertura mínima exigida:
   - Services: 90% linhas, 80% branches
   - Controllers: 80% linhas
   - Geral: 80% linhas

## 🔑 Regras de Negócio Principais

1. Uma pauta pode ter apenas uma sessão de votação
2. Por padrão, a sessão de votação dura 1 minuto
3. Cada CPF pode votar apenas uma vez por pauta
4. Opções de voto válidas são: "SIM" ou "NÃO"
5. CPFs são validados antes do registro do voto
6. A votação é encerrada automaticamente após o tempo definido
7. A votação pode ser encerrada manualmente, mas não se já possuirem votos

## 🗃️ Estrutura do Banco de Dados

- **associados**: Armazena os associados
- **pautas**: Armazena as pautas para votação
- **sessao_votacao**: Registra as sessões de votação
- **voto**: Registra os votos dos associados

---
Desenvolvido com ❤️ por Camila Barpp