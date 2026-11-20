# JSON Dataset Service

A RESTful Spring Boot microservice that stores schema-less JSON records inside a relational database (MySQL) and supports dynamic runtime **grouping** and **sorting** by any JSON field.

## Tech Stack

- **Java 21** (Records, Pattern Matching)
- **Spring Boot 4.1.2** (Spring Web, Spring Data JPA, Validation)
- **MySQL 8** (Native `JSON` column via Hibernate `@JdbcTypeCode(SqlTypes.JSON)`)
- **H2 Database** (In-memory MySQL-compatibility mode for automated tests)
- **JUnit & MockMvc** (Unit and end-to-end integration testing)
- **Maven** (with Maven Wrapper)

## Architecture & Design Patterns

The application follows a **Layered Architecture** paired with the **Strategy** and **Factory** design patterns so new query operators (such as filtering or pagination) can be added without modifying existing service logic.

```text
src/main/java/com/project/
├── controller/   # REST endpoints and request mapping
├── dto/          # Immutable Java record response payloads
├── entity/       # JPA entity mapping dynamic JSON to MySQL
├── exception/    # Custom exceptions and @RestControllerAdvice handler
├── repository/   # Spring Data JPA repository
├── service/      # Business logic and transaction boundaries
└── strategy/     # QueryOperationStrategy, GroupByStrategy, SortByStrategy, QueryStrategyFactory
```

## Prerequisites

- **JDK 21** or higher
- **MySQL 8.x** running locally (only required to run the main app; automated tests run in-memory with H2)

## Setup & Running Locally

### 1. Clone the Repository

```bash
git clone [https://github.com/Bhaskar-2541/json-dataset-service.git](https://github.com/Bhaskar-2541/json-dataset-service.git)
cd json-dataset-service
```

### 2. Configure MySQL

By default, the application connects to `jdbc:mysql://localhost:3306/dataset_db` and creates the database and `dataset_records` table automatically on startup.

Update `src/main/resources/application.properties` or pass environment variables:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/dataset_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
```

### 3. Run the Application

**Windows (PowerShell / CMD):**
```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux / Git Bash:**
```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`.

## Running Automated Tests

All unit and integration tests run under the `test` profile against an in-memory H2 database—no local MySQL instance is required.

**Windows:**
```powershell
.\mvnw.cmd test
```

**macOS / Linux / Git Bash:**
```bash
./mvnw test
```

## API Reference

### 1. Insert a JSON Record

Stores an arbitrary JSON object under the specified `{datasetName}`.

- **Endpoint:** `POST /api/dataset/{datasetName}/record`
- **Content-Type:** `application/json`

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/dataset/employee_dataset/record \
  -H "Content-Type: application/json" \
  -d '{"id": 1, "name": "John Doe", "age": 30, "department": "Engineering"}'
```

**Response (`201 Created`):**
```json
{
  "message": "Record added successfully",
  "dataset": "employee_dataset",
  "recordId": 1
}
```

---

### 2. Query Records — Group By Field

Groups all records in `{datasetName}` by any field key.

- **Endpoint:** `GET /api/dataset/{datasetName}/query?groupBy={field}`

**Example Request:**
```bash
curl "http://localhost:8080/api/dataset/employee_dataset/query?groupBy=department"
```

**Response (`200 OK`):**
```json
{
  "groupedRecords": {
    "Engineering": [
      { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" },
      { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" }
    ],
    "Marketing": [
      { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" }
    ]
  }
}
```

---

### 3. Query Records — Sort By Field

Sorts all records in `{datasetName}` numerically (for numbers) or lexicographically (for strings), placing `null` or missing values last.

- **Endpoint:** `GET /api/dataset/{datasetName}/query?sortBy={field}&order={asc|desc}`
- **`order`** is optional and defaults to `asc`.

**Example Request:**
```bash
curl "http://localhost:8080/api/dataset/employee_dataset/query?sortBy=age&order=asc"
```

**Response (`200 OK`):**
```json
{
  "sortedRecords": [
    { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" },
    { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" },
    { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" }
  ]
}
```

---

### Error Responses

All errors return a structured JSON payload via `GlobalExceptionHandler`:

| HTTP Status | Trigger |
| :--- | :--- |
| `400 Bad Request` | Empty/malformed JSON body, missing `groupBy`/`sortBy`, both `groupBy` and `sortBy` provided together, invalid `order` value, or non-existent field name |
| `404 Not Found` | Querying a `{datasetName}` that has no stored records |

**Example Error Response (`404 Not Found`):**
```json
{
  "timestamp": "2026-11-20T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "No records found for dataset: 'unknown_dataset'",
  "path": "/api/dataset/unknown_dataset/query"
}
```