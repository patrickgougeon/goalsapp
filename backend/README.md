# Metas API — Checklist e Acompanhamento de Metas

Backend REST do aplicativo Android de checklist e acompanhamento de metas. O usuário cadastra metas,
divide cada uma em itens de checklist e acompanha o progresso. A API processa esses dados e gera um
**resultado personalizado de desempenho**: pontuação, perfil e recomendações.

## Tecnologias

- Java 21 + Spring Boot 4.1 (Web MVC, Data JPA, Validation)
- PostgreSQL 17 (via Docker)
- springdoc-openapi (Swagger UI)
- BCrypt (spring-security-crypto) para hash de senhas
- Lombok
- JUnit 5 + MockMvc + H2 (testes)

## Como executar

### Opção 1 — tudo no Docker (API + banco)

```bash
docker compose up --build
```

### Opção 2 — banco no Docker, API local

```bash
docker compose up -d db
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`.

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI (JSON):** http://localhost:8080/v3/api-docs

> No emulador Android, acesse a API pelo endereço `http://10.0.2.2:8080`. Em um dispositivo físico,
> use o IP da máquina na rede local (ex.: `http://192.168.0.10:8080`).

### Variáveis de ambiente

| Variável      | Padrão                                   |
|---------------|------------------------------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:5432/metas` |
| `DB_USERNAME` | `metas`                                  |
| `DB_PASSWORD` | `metas`                                  |
| `SERVER_PORT` | `8080`                                   |

### Testes

```bash
./mvnw test
```

Os testes usam banco H2 em memória e não dependem do Docker.

## Estrutura do projeto

```
src/main/java/br/com/metas/api
├── config       # OpenAPI e encoder de senha
├── controller   # Endpoints REST
├── dto          # Objetos de requisição/resposta (records)
├── entity       # Entidades JPA e enums
├── exception    # Exceções e tratamento global de erros
├── repository   # Repositórios Spring Data
└── service      # Regras de negócio
```

## Modelo de dados

- **Usuario** — nome, e-mail (único), senha (hash BCrypt)
- **Meta** — título, descrição, categoria, prioridade, status, data limite; pertence a um usuário
- **ItemChecklist** — descrição, ordem, concluído; pertence a uma meta

Enums:

- `Categoria`: `SAUDE`, `ESTUDOS`, `CARREIRA`, `FINANCAS`, `PESSOAL`, `LAZER`, `OUTROS`
- `Prioridade`: `BAIXA`, `MEDIA`, `ALTA`
- `StatusMeta`: `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`

## Endpoints

### Usuários

| Método | Rota                              | Descrição                              |
|--------|-----------------------------------|----------------------------------------|
| POST   | `/api/usuarios`                   | Cadastra usuário                       |
| POST   | `/api/usuarios/login`             | Autentica por e-mail e senha           |
| GET    | `/api/usuarios/{id}`              | Consulta usuário                       |
| PUT    | `/api/usuarios/{id}`              | Altera usuário                         |
| DELETE | `/api/usuarios/{id}`              | Remove usuário e suas metas            |
| GET    | `/api/usuarios/{id}/desempenho`   | **Resultado personalizado**            |

### Metas

| Método | Rota                                   | Descrição                                     |
|--------|----------------------------------------|-----------------------------------------------|
| GET    | `/api/usuarios/{usuarioId}/metas`      | Lista metas (filtros `?status=` `&categoria=`) |
| POST   | `/api/usuarios/{usuarioId}/metas`      | Cadastra meta (pode incluir itens iniciais)   |
| GET    | `/api/metas/{id}`                      | Consulta meta com checklist e progresso       |
| PUT    | `/api/metas/{id}`                      | Altera meta                                   |
| PATCH  | `/api/metas/{id}/status`               | Altera status                                 |
| DELETE | `/api/metas/{id}`                      | Remove meta                                   |

### Checklist

| Método | Rota                          | Descrição                        |
|--------|-------------------------------|----------------------------------|
| GET    | `/api/metas/{metaId}/itens`   | Lista itens da meta              |
| POST   | `/api/metas/{metaId}/itens`   | Adiciona item                    |
| PUT    | `/api/itens/{id}`             | Altera item                      |
| PATCH  | `/api/itens/{id}/alternar`    | Marca/desmarca item              |
| DELETE | `/api/itens/{id}`             | Remove item                      |

### Regras de negócio

- O **progresso** de uma meta é o percentual de itens concluídos.
- Quando todos os itens são marcados, a meta passa para `CONCLUIDA` automaticamente; ao desmarcar
  um item, ela volta para `EM_ANDAMENTO`.
- Uma meta `EM_ANDAMENTO` com data limite no passado é sinalizada como **atrasada**.
- O checklist de uma meta `CANCELADA` não pode ser alterado.

### Resultado personalizado (`/desempenho`)

Considerando as metas não canceladas, a pontuação (0–100) é:

```
pontuação = 40% × taxa de conclusão + 40% × progresso médio + 20% × pontualidade
```

| Pontuação | Perfil          |
|-----------|-----------------|
| sem metas | `INICIANTE`     |
| 0–34      | `EM_CONSTRUCAO` |
| 35–59     | `EM_EVOLUCAO`   |
| 60–84     | `COMPROMETIDO`  |
| 85–100    | `EXEMPLAR`      |

A resposta também traz o progresso por categoria, a categoria em destaque e recomendações geradas a
partir das metas (atrasos, prazos próximos, metas de prioridade alta com pouco progresso, metas sem
checklist e a categoria mais fraca).

### Exemplo

```http
POST /api/usuarios/1/metas
Content-Type: application/json

{
  "titulo": "Correr 5 km",
  "descricao": "Conseguir correr 5 km sem parar",
  "categoria": "SAUDE",
  "prioridade": "ALTA",
  "dataLimite": "2026-12-31",
  "itens": [
    { "descricao": "Correr 2 km" },
    { "descricao": "Correr 3,5 km" },
    { "descricao": "Correr 5 km" }
  ]
}
```

### Erros

Os erros seguem um formato padrão:

```json
{
  "timestamp": "2026-11-14T13:00:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados inválidos",
  "detalhes": ["email: must be a well-formed email address"]
}
```

| Status | Situação                                   |
|--------|--------------------------------------------|
| 400    | Validação ou JSON inválido                 |
| 401    | Login com credenciais inválidas            |
| 404    | Recurso não encontrado                     |
| 409    | Regra de negócio (e-mail duplicado, etc.)  |

## Screenshots

_Adicionar prints do aplicativo Android aqui._
