# Schema Registry with Avro and PostgreSQL

A lightweight schema registry service built with Spring Boot that manages Avro schemas and stores them in PostgreSQL. This service provides a REST API for registering schemas and retrieving the latest versions.

## Features

- **Schema Registration**: Register Avro schemas with automatic version management
- **Schema Validation**: Validates Avro schema format before storing
- **Fingerprinting**: Uses SHA-256 fingerprinting to detect duplicate schemas
- **Version Control**: Automatically manages schema versions per subject
- **REST API**: Simple REST endpoints for schema operations
- **PostgreSQL Database**: Persistent schema storage with unique constraints
- **Docker Support**: Containerized application with Docker Compose

## Technology Stack

- **Java 17**: Latest LTS version
- **Spring Boot 3.2.0**: Modern Spring framework
- **PostgreSQL 15**: Relational database
- **Apache Avro 1.11.3**: Schema serialization framework
- **Docker & Docker Compose**: Container orchestration

## Project Structure

```
.
├── src/
│   └── main/
│       ├── java/com/example/sr/
│       │   ├── SchemaRegistryApplication.java    # Main application class
│       │   ├── controller/
│       │   │   └── SchemaController.java         # REST API endpoints
│       │   ├── service/
│       │   │   └── SchemaRegistryService.java    # Business logic
│       │   ├── repository/
│       │   │   └── SchemaRepository.java         # Database operations
│       │   ├── model/
│       │   │   └── SchemaEntity.java             # Schema data model
│       │   └── dto/
│       │       └── RegisterSchemaRequest.java    # Request DTO
│       └── resources/
│           └── application.yml                   # Spring Boot configuration
├── db/
│   └── init.sql                                  # Database schema initialization
├── docker-compose.yml                            # Docker Compose configuration
├── Dockerfile                                    # Application Docker image
└── pom.xml                                       # Maven project configuration
```

## Getting Started

### Prerequisites

- Docker and Docker Compose installed
- OR: Java 17+, Maven 3.6+, and PostgreSQL 15+ (for local development)

### Quick Start with Docker Compose

1. Clone the repository:
```bash
git clone <repository-url>
cd schema-registry-avro-pg
```

2. Start the application and PostgreSQL:
```bash
docker-compose up -d
```

3. Verify the service is running:
```bash
curl http://localhost:8081/subjects
```

### Local Development Setup

1. Install dependencies:
```bash
mvn clean install
```

2. Configure PostgreSQL connection in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/schema_registry
    username: sr
    password: sr
```

3. Create the database and run init script:
```bash
# Create database
createdb -U postgres schema_registry

# Run init script
psql -U postgres schema_registry < db/init.sql
```

4. Build and run:
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8081`

## API Endpoints

### Register a Schema

**POST** `/subjects/{subject}/versions`

Register a new Avro schema for a subject.

**Request Body:**
```json
{
  "schema": "{\"type\":\"record\",\"name\":\"User\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"string\"}]}"
}
```

**Response:**
```json
{
  "id": 1,
  "version": 1
}
```

**Status Codes:**
- `200 OK`: Schema registered successfully
- `400 Bad Request`: Invalid Avro schema format

### Get Latest Schema Version

**GET** `/subjects/{subject}/versions/latest`

Retrieve the latest version of a schema for a given subject.

**Response:**
```json
{
  "id": 1,
  "version": 1,
  "subject": "User",
  "schema": "{...}"
}
```

**Status Codes:**
- `200 OK`: Schema found
- `404 Not Found`: Subject not found

## Database Schema

The PostgreSQL database uses the following table:

```sql
CREATE TABLE schemas (
  id SERIAL PRIMARY KEY,
  subject TEXT NOT NULL,
  version INT NOT NULL,
  schema TEXT NOT NULL,
  fingerprint TEXT NOT NULL UNIQUE,
  UNIQUE(subject, version)
);
```

### Table Details

- **id**: Auto-incrementing primary key
- **subject**: Schema subject name (e.g., "User", "Order")
- **version**: Version number for the subject (auto-incremented per subject)
- **schema**: The Avro schema in JSON format
- **fingerprint**: SHA-256 hash of the schema to detect duplicates

## How It Works

1. **Schema Registration**: When a schema is submitted:
   - The schema text is validated as valid Avro format
   - A SHA-256 fingerprint is computed from the schema
   - If the fingerprint exists, the existing schema is returned
   - Otherwise, a new version is created and stored

2. **Version Management**: Each subject maintains its own version counter, automatically incremented for new schemas

3. **Duplicate Detection**: Identical schemas (same fingerprint) are not stored twice, ensuring schema deduplication

## Configuration

### Environment Variables

Configure via Docker environment or `application.yml`:

```yaml
server:
  port: 8081  # Service port

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/schema_registry
    username: sr
    password: sr
```

### Docker Compose Variables

```yaml
POSTGRES_DB: schema_registry
POSTGRES_USER: sr
POSTGRES_PASSWORD: sr
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/schema_registry
SPRING_DATASOURCE_USERNAME: sr
SPRING_DATASOURCE_PASSWORD: sr
```

## Building and Deployment

### Build Docker Image

```bash
docker build -t schema-registry:latest .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

### View Logs

```bash
docker-compose logs -f schema-registry
```

### Stop Services

```bash
docker-compose down
```

## Development

### Building the Project

```bash
mvn clean build
```

### Running Tests

```bash
mvn test
```

### Creating an Executable JAR

```bash
mvn clean package
java -jar target/schema-registry-avro-pg-0.0.1.jar
```

## Example Usage

### Register a User Schema

```bash
curl -X POST http://localhost:8081/subjects/User/versions \
  -H "Content-Type: application/json" \
  -d '{
    "schema": "{\"type\":\"record\",\"name\":\"User\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"string\"}]}"
  }'
```

Response:
```json
{"id": 1, "version": 1}
```

### Register an Order Schema

```bash
curl -X POST http://localhost:8081/subjects/Order/versions \
  -H "Content-Type: application/json" \
  -d '{
    "schema": "{\"type\":\"record\",\"name\":\"Order\",\"fields\":[{\"name\":\"order_id\",\"type\":\"string\"},{\"name\":\"total\",\"type\":\"double\"}]}"
  }'
```

### Get Latest User Schema

```bash
curl http://localhost:8081/subjects/User/versions/latest
```

Response:
```json
{
  "id": 1,
  "version": 1,
  "subject": "User",
  "schema": "{\"type\":\"record\",\"name\":\"User\",\"fields\":[...]}"
}
```

## Troubleshooting

### Database Connection Issues

- Verify PostgreSQL is running: `docker-compose ps`
- Check connection string in `application.yml`
- Ensure database exists and init script was executed

### Invalid Schema Errors

- Verify your Avro schema is valid JSON
- Use an Avro schema validator to test your schema
- Common issues: missing required fields, incorrect type definitions

### Port Already in Use

- Change port in `docker-compose.yml` (default 8081)
- Or stop the service using the port: `lsof -i :8081`

## License

This project is provided as-is for educational and commercial purposes.

## Support

For issues, questions, or contributions, please contact the development team.
