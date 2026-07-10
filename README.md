# Bank API

API REST simples de operações bancárias (cadastro de cliente, autenticação, abertura e movimentação de conta), feita em Spring Boot 3 / Java 21, com PostgreSQL rodando em Docker.

## Stack

- Java 21
- Spring Boot 3.5.16 (Web, Data JPA, Security, OAuth2 Resource Server, Validation)
- PostgreSQL 16 (via Docker)
- JWT (assinatura HMAC/HS256, usando `nimbus-jose-jwt`, já incluso no starter do OAuth2 Resource Server)
- Lombok
- springdoc-openapi (Swagger UI)
- Maven (com wrapper `mvnw`, não precisa ter Maven instalado)

## Pré-requisitos

- Java 21
- Docker e Docker Compose

## Como rodar

1. Suba o banco de dados:

   ```bash
   docker compose up -d
   ```

   Confirme que o container está saudável:

   ```bash
   docker compose ps
   ```

2. Configure as variáveis de ambiente. Copie o `.env.example` para `.env` e ajuste os valores (principalmente `JWT_SECRET`, que deve ser uma string aleatória forte):

   ```bash
   cp .env.example .env
   ```

3. Rode a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

A aplicação sobe em `http://localhost:8080`.

## Documentação interativa (Swagger)

Com a aplicação rodando:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Autenticação

A maior parte das rotas exige um token JWT. Fluxo:

1. Cadastre um cliente: `POST /clientes` (rota pública).
2. Faça login: `POST /auth/login` (rota pública) — retorna um token.
3. Envie o token nas demais requisições, no header:

   ```
   Authorization: Bearer <token>
   ```

Rotas públicas (sem autenticação): `POST /clientes`, `POST /auth/login` e o Swagger. Todas as demais exigem token válido.

## Endpoints implementados

### Cliente

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| POST | `/clientes` | Cadastra um cliente | Pública |
| GET | `/clientes?nome=` | Lista clientes (filtro opcional por nome) | Sim |
| GET | `/clientes/{id}` | Busca cliente por id | Sim |
| PUT | `/clientes/{id}` | Atualiza nome/email do cliente | Sim |
| DELETE | `/clientes/{id}` | Remove cliente (bloqueado se houver conta vinculada) | Sim |

### Autenticação

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| POST | `/auth/login` | Autentica com email/senha e retorna um JWT | Pública |

### Conta

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| POST | `/contas` | Abre uma conta para um cliente existente | Sim |
| GET | `/contas` | Lista todas as contas | Sim |
| GET | `/contas/{id}` | Busca conta por id | Sim |
| POST | `/contas/{id}/depositar` | Deposita um valor na conta | Sim |
| POST | `/contas/{id}/sacar` | Saca um valor da conta (valida saldo disponível) | Sim |
| GET | `/contas/{id}/saldo` | Consulta o saldo atual da conta | Sim |
| GET | `/contas/{id}/extrato` | Lista os lançamentos da conta (mais recente primeiro) | Sim |
| POST | `/contas/{id}/transferir` | Transfere um valor para outra conta (identificada por número + agência) | Sim |
| PATCH | `/contas/{id}/encerrar` | Encerra a conta (só com saldo zerado) | Sim |


## Estrutura do projeto

```
src/main/java/com/aplication/bankapi
 ├─ entity/          # Entidades JPA (Cliente, Conta, Lancamento)
 ├─ enums/           # Enums de domínio (StatusConta, TipoLancamento)
 ├─ dto/             # Records de request/response, organizados por feature
 ├─ repository/      # Interfaces Spring Data JPA
 ├─ service/         # Regras de negócio
 ├─ controller/      # Endpoints REST
 ├─ exception/       # Exceptions de negócio
 │   ├─ errors/      # ErrorResponse / ValidationErrorResponse (schemas de erro)
 │   └─ handler/     # GlobalExceptionHandler (@RestControllerAdvice)
 └─ config/          # Segurança (JWT, PasswordEncoder) e OpenAPI/Swagger
```

## Banco de dados

O schema é criado/atualizado automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`) a partir das entidades — não há scripts de migration manuais. Se o schema ficar inconsistente durante o desenvolvimento (ex: renomear coluna), a forma mais simples de resetar é:

```bash
docker compose down -v
docker compose up -d
```