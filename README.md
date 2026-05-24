# Todo API

CRUD REST API for a todo app built with Quarkus, Jakarta REST/JPA, Hibernate ORM Panache, Flyway, and PostgreSQL.

## Requirements

- Java 21
- Maven 3.9+
- Docker, for local PostgreSQL

## Run Locally

Start Postgres:

```bash
docker compose up -d
```

Run Quarkus in dev mode:

```bash
mvn quarkus:dev
```

The API runs at `http://localhost:8080`.

Swagger UI is available at `http://localhost:8080/q/swagger-ui`.

The OpenAPI document is available at `http://localhost:8080/q/openapi`.

## Configuration

Defaults are set in `src/main/resources/application.properties`.

| Environment variable | Default |
| --- | --- |
| `DB_JDBC_URL` | `jdbc:postgresql://localhost:5432/todo_db` |
| `DB_USERNAME` | `todo_user` |
| `DB_PASSWORD` | `todo_password` |
| `CORS_ORIGINS` | `http://localhost:3000,http://localhost:5173` |

## Endpoints

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/todos` | List todos. Optional query params: `completed`, `q`, `page`, `size`, `sort`, `direction` |
| `GET` | `/api/todos/{id}` | Get one todo |
| `POST` | `/api/todos` | Create a todo |
| `PUT` | `/api/todos/{id}` | Replace a todo |
| `PATCH` | `/api/todos/{id}/complete` | Update completion status |
| `DELETE` | `/api/todos/{id}` | Delete a todo |

List responses are paginated:

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalItems": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

Errors use a consistent JSON shape:

```json
{
  "error": "BAD_REQUEST",
  "message": "size must be between 1 and 100",
  "status": 400,
  "path": "api/todos",
  "timestamp": "2026-05-23T02:36:40Z",
  "fields": []
}
```

## Example Requests

Create:

```bash
curl -X POST http://localhost:8080/api/todos \
  -H 'Content-Type: application/json' \
  -d '{"title":"Build frontend","description":"Wire UI to REST API"}'
```

Update:

```bash
curl -X PUT http://localhost:8080/api/todos/{id} \
  -H 'Content-Type: application/json' \
  -d '{"title":"Build frontend","description":"Wire UI to REST API","completed":false}'
```

Complete:

```bash
curl -X PATCH http://localhost:8080/api/todos/{id}/complete \
  -H 'Content-Type: application/json' \
  -d '{"completed":true}'
```

## Test

```bash
mvn test
```
