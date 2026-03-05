# hellapi

Spring Boot REST API for product management and role/permission control.

## Features

- Product CRUD with soft delete and restore.
- Trade CRUD with soft delete and restore.
- Role management with permission assignment.
- HTTP Basic auth + method-level authorization.
- Swagger UI documentation.
- MySQL runtime database; H2 for tests.

## Requirements

- JDK 17
- Maven (or the bundled `./mvnw`)

## Quick Start

1) Start MySQL (creates `hellapi` database automatically):

   ```bash
   docker compose up -d mysql
   ```

   Or if using an existing MySQL, create the database manually:

   ```bash
   mysql -h 127.0.0.1 -P 49154 -u root -pmysqlpw -e "CREATE DATABASE IF NOT EXISTS hellapi;"
   ```

2) Run the app:

```
./mvnw spring-boot:run
```

3) Open Swagger UI:

```
http://localhost:8090/swagger-ui/index.html
```

## Default Accounts

HTTP Basic authentication is enabled for `/api/**`.

| Username     | Password          | Role          |
|--------------|-------------------|---------------|
| systemadmin  | systemadmin123    | SYSTEM_ADMIN  |
| admin        | admin123          | ADMIN         |
| user         | user123           | USER          |

`SYSTEM_ADMIN` has full access. `ADMIN`/`USER` permissions come from the roles table.

## API Endpoints

- `POST /api/products`
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/products/deleted`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`
- `PUT /api/products/{id}/restore`

- `POST /api/trades`
- `GET /api/trades`
- `GET /api/trades/{id}`
- `GET /api/trades/deleted`
- `PUT /api/trades/{id}`
- `DELETE /api/trades/{id}`
- `PUT /api/trades/{id}/restore`

- `POST /api/roles`
- `GET /api/roles`
- `GET /api/roles/{id}`
- `PUT /api/roles/{id}/permissions`

## Database

The `hellapi` database must exist. Tables are created by Hibernate (`ddl-auto: update`). No schema or seed scripts are run.

```bash
mysql -h 127.0.0.1 -P 49154 -u root -pmysqlpw -e "CREATE DATABASE IF NOT EXISTS hellapi;"
```

## Tests

```
./mvnw test
```

Tests use the H2 in-memory database defined in `src/test/resources/application-test.yml`.
