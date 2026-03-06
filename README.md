# hellapi

Spring Boot REST API for product management and role/permission control.

## Features

- Product CRUD with soft delete and restore.
- Trade CRUD with soft delete and restore.
- Singapore stock orders with Moomoo OpenAPI integration (buy/sell).
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

- `POST /api/singapore-stocks/orders` - Place buy/sell order (Singapore stocks via Moomoo)
- `GET /api/singapore-stocks/orders`
- `GET /api/singapore-stocks/orders/{id}`
- `DELETE /api/singapore-stocks/orders/{id}`
- `GET /api/singapore-stocks/status` - Check Moomoo OpenAPI availability

- `POST /api/roles`
- `GET /api/roles`
- `GET /api/roles/{id}`
- `PUT /api/roles/{id}/permissions`

## Singapore Stock Trading (Moomoo OpenAPI)

To trade Singapore stocks (buy/sell), integrate with [Moomoo OpenAPI](https://openapi.moomoo.com/moomoo-api-doc/):

1. Download and run [OpenD](https://openapi.moomoo.com/moomoo-api-doc/en/quick/opend-base.html) - the gateway for Moomoo API
2. Login with your Moomoo SG account in OpenD
3. Set in `application.yml`:
   ```yaml
   moomoo:
     enabled: true
     host: 127.0.0.1
     port: 11111
     paper-trading: true   # false for live trading
   ```
4. Use symbols like `D05` (DBS), `O39` (OCBC) - they are sent as `SG.D05`, `SG.O39` to the API

When `moomoo.enabled=false` (default), orders are stored locally but not submitted to Moomoo.

## Auto-Sell Schedule (scheduleTrade)

A scheduled job checks Singapore stock prices every 5 minutes and auto-sells when the current price is **10% or more** above the bought price.

- **Package:** `com.example.hellapi.scheduletrade`
- **Config:** Set `schedule-trade.enabled: true` in `application.yml`
- **Logic:** For each BUY order (status SUBMITTED or FILLED) not yet auto-sold, fetches quote from Moomoo; if `currentPrice >= boughtPrice * 1.10`, places a MARKET SELL order
- **Threshold:** Configurable via `schedule-trade.auto-sell-threshold-percent` (default: 10)
- **Interval:** Configurable via `schedule-trade.interval-ms` (default: 300000 = 5 minutes)

Requires `moomoo.enabled=true` for real-time quotes. When disabled, no quotes are available and no auto-sells occur.

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
