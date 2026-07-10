# Social Media Backend

Kotlin/Spring Boot microservice backend for a social media MVP.

## Services

- `apigateway` on port `8080`: validates JWTs, signs internal requests, routes traffic.
- `authservice` on port `8083`: registration, login, JWT issuing.
- `userservice` on port `8081`: profiles, search, follows.
- `postservice` on port `8082`: posts, feed, likes, comments.
- `notificationservice` on port `8084`: stores and reads notifications from MongoDB, consumes social events from Kafka.

## Infrastructure

`docker-compose.yml` starts:

- PostgreSQL `localhost:5432`
- Redis `localhost:6379`
- MongoDB `localhost:27017`
- Kafka `localhost:9092`

## Requirements

- JDK 21
- Docker

The Gradle build is configured with a Java 21 toolchain. If only another JDK is installed, Gradle will fail before compiling.

## Run Locally

Start infrastructure:

```powershell
docker compose up -d
```

Run all tests:

```powershell
.\gradlew.bat test
```

Run a service:

```powershell
.\gradlew.bat :apigateway:bootRun
.\gradlew.bat :authservice:bootRun
.\gradlew.bat :userservice:bootRun
.\gradlew.bat :postservice:bootRun
.\gradlew.bat :notificationservice:bootRun
```

Use the API through the gateway at `http://localhost:8080`.

## Main API Flow

1. Register: `POST /auth/register`
2. Login: `POST /auth/login`
3. A basic user profile is created automatically from the registration event.
4. Create/read posts: `/posts`
5. Follow users: `POST /users/{id}/follow`
6. Read notifications: `GET /notifications`
7. Mark notifications read: `PATCH /notifications/{id}/read` or `PATCH /notifications/read-all`

Authenticated gateway requests need:

```http
Authorization: Bearer <accessToken>
```

Internal services expect gateway-signed headers and should be called through `apigateway` in normal use.

## Environment

Useful overrides:

- `JWT_SECRET`
- `GATEWAY_INTERNAL_SIGNING_SECRET`
- `AUTHSERVICE_BASE_URL`
- `USERSERVICE_BASE_URL`
- `POSTSERVICE_BASE_URL`
- `NOTIFICATIONSERVICE_BASE_URL`
- `CORS_ALLOWED_ORIGINS`

Use secrets at least 32 bytes long for JWT and gateway signing.
