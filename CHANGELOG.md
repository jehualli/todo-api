# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added

- Created a Quarkus-based Todo REST API project using Java 21 and Maven.
- Added Jakarta REST endpoints for todo CRUD operations:
  - `GET /api/todos`
  - `GET /api/todos/{id}`
  - `POST /api/todos`
  - `PUT /api/todos/{id}`
  - `PATCH /api/todos/{id}/complete`
  - `DELETE /api/todos/{id}`
- Added PostgreSQL persistence with Hibernate ORM Panache.
- Added Flyway database migration for the `todos` table.
- Added UUID primary keys for todos.
- Added todo fields:
  - `title`
  - `description`
  - `completed`
  - `createdAt`
  - `updatedAt`
- Added automatic timestamp handling for create and update operations.
- Added request DTOs for create, replace, and completion-status updates.
- Added response DTOs for todo API output.
- Added input validation for todo request payloads.
- Added local PostgreSQL setup with `docker-compose.yml`.
- Added application configuration for:
  - PostgreSQL datasource
  - Flyway migrations
  - CORS for frontend development ports
  - Swagger UI
  - OpenAPI document generation
- Added Swagger UI support at `/q/swagger-ui/`.
- Added OpenAPI document endpoint at `/q/openapi`.
- Added explicit OpenAPI metadata through `OpenApiConfig`.
- Added OpenAPI operation docs and response schemas for todo endpoints.
- Added paginated todo list responses with `TodoPageResponse`.
- Added list query parameters:
  - `completed`
  - `q`
  - `page`
  - `size`
  - `sort`
  - `direction`
- Added sort support for:
  - `createdAt`
  - `updatedAt`
  - `title`
  - `completed`
- Added `TodoService` to separate REST-layer concerns from business and persistence logic.
- Added structured JSON API errors with `ApiError`.
- Added exception mappers for:
  - validation errors
  - bad requests
  - not found errors
  - generic web application errors
- Added REST-assured tests covering:
  - create and read
  - list with filters
  - update
  - completion toggle
  - delete
  - validation errors
  - invalid pagination
- Added README documentation with:
  - requirements
  - local run instructions
  - configuration variables
  - endpoint list
  - example curl requests
  - paginated response shape
  - error response shape

### Changed

- Changed `GET /api/todos` from returning a raw JSON array to returning a paginated response object.
- Moved todo lookup, creation, update, completion, deletion, filtering, pagination, and sorting logic out of `TodoResource` and into `TodoService`.
- Replaced deprecated Quarkus Hibernate schema-generation configuration with `quarkus.hibernate-orm.schema-management.strategy`.
- Updated Quarkus test dependencies from relocated artifact names to the current names:
  - `quarkus-junit`
  - `quarkus-junit-mockito`
- Improved OpenAPI schemas so paginated todo responses show `items` as `TodoResponse[]`.
- Improved error responses so frontend code can rely on a consistent JSON shape.

### Verified

- Started PostgreSQL locally with Docker Compose.
- Ran the Quarkus app in dev mode through a Maven/JDK Docker container because the host machine did not have Java or Maven installed.
- Verified Flyway applied the initial database migration.
- Verified Swagger UI is reachable at `http://localhost:8080/q/swagger-ui/`.
- Verified OpenAPI is reachable at `http://localhost:8080/q/openapi`.
- Verified todo creation through `POST /api/todos`.
- Verified paginated listing through `GET /api/todos?page=0&size=5`.
- Verified structured bad-request response for invalid pagination.
- Verified structured validation response for invalid todo creation.

### Notes

- The API is currently running in Quarkus dev mode inside Docker.
- PostgreSQL is currently running in Docker through `docker-compose.yml`.
- The frontend has not been added to this repository.
