# Twilio SMS Service

This project is a demo Spring Boot 3 application for sending and tracking SMS messages using the Twilio API.

## Features

- **Send SMS**: REST API to send SMS messages via Twilio.
- **Track Status**: Store and retrieve SMS status, including delivery status and errors.
- **Persistence**: Uses PostgreSQL (with Docker support) and Spring Data JPA (Hibernate).
- **Testing**: Includes unit and integration tests with JUnit 5, Mockito, and Testcontainers.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA (Hibernate)
- PostgreSQL
- Twilio SDK
- Maven

## Getting Started

### Prerequisites

- Java 17+
- Maven
- Docker (for PostgreSQL in development/testing)

### Configuration

Set your Twilio credentials and database connection in `src/main/resources/application.properties`:

```properties
twilio.account.sid=your_account_sid
twilio.auth.token=your_auth_token
spring.datasource.url=jdbc:postgresql://localhost:5432/twiliodb
spring.datasource.username=postgres
spring.datasource.password=password
```

### Running the Application

1. Start PostgreSQL (e.g., via Docker):

    ```sh
    docker run --name twilio-postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=twiliodb -p 5432:5432 -d postgres:17-alpine
    ```

2. Build and run:

    ```sh
    ./mvnw spring-boot:run
    ```

### API Endpoints

- **POST `/api/sms`**: Send an SMS message.
- **GET `/api/sms?status=STATUS`**: Retrieve sent SMS messages, optionally filtered by status.

Example request to send SMS:

```http
POST /api/sms
Content-Type: application/json

{
  "to": "+1234567890",
  "from": "+0987654321",
  "body": "Hello from Twilio!"
}
```

## Testing

Run all tests:

```sh
./mvnw test
```

Integration tests use Testcontainers to spin up a PostgreSQL instance automatically.

## References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/index.html)
- [Twilio Messaging Quickstart](https://www.twilio.com/docs/messaging/quickstart)

---

