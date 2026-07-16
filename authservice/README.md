# Auth Service

`authservice` owns authentication accounts. It registers users, verifies credentials, issues JWT access tokens, manages refresh tokens, records audit events, and publishes registration events for profile creation.

## Responsibilities

- Store auth accounts in PostgreSQL.
- Hash passwords with BCrypt.
- Normalize emails before lookup and registration.
- Issue JWT access tokens with roles and scope claims.
- Issue and revoke refresh tokens stored as SHA-256 hashes.
- Record auth audit events for register, login, refresh, and logout.
- Store registration events in `auth_outbox_events` and publish them to Kafka topic `social.users`.
- Keep auth data separate from user profile data.

## Runtime

| Property | Value |
| --- | --- |
| Port | `8083` |
| Database | PostgreSQL |
| Migrations | Flyway, table `flyway_schema_history_auth` |
| Messaging | Kafka producer |
| Public paths | `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`, `/actuator/health`, `/actuator/info`, `/actuator/prometheus` |

## API

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/auth/register` | Create account and return token |
| `POST` | `/auth/login` | Verify credentials and return token |
| `POST` | `/auth/refresh` | Rotate refresh token and return a new token pair |
| `POST` | `/auth/logout` | Revoke refresh token |

Register/login request:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Response:

```json
{
  "userId": "00000000-0000-0000-0000-000000000000",
  "tokenType": "Bearer",
  "accessToken": "<jwt>",
  "refreshToken": "<opaque-refresh-token>",
  "expiresAt": "2026-01-01T12:00:00Z"
}
```

Refresh/logout request:

```json
{
  "refreshToken": "<opaque-refresh-token>"
}
```

## Events

Produces through transactional outbox:

| Topic | Event | When |
| --- | --- | --- |
| `social.users` | `UserRegisteredEvent` | After successful registration |

`userservice` consumes this event and creates a default profile for the new account.

Outbox records are retried, locked during publish attempts, marked `FAILED` after the retry limit, and sent to `social.users.DLT`.

## Configuration

| Property | Default |
| --- | --- |
| `server.port` | `8083` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/social_media` |
| `spring.datasource.username` | `social` |
| `spring.datasource.password` | `social` |
| `spring.kafka.bootstrap-servers` | `localhost:9092` |
| `security.jwt.secret` | `${JWT_SECRET:change-this-secret-to-at-least-32-characters}` |
| `security.jwt.access-token-ttl` | `1h` |
| `security.jwt.refresh-token-ttl` | `30d` |
| `users.kafka.topic` | `social.users` |
| `outbox.max-attempts` | `5` |
| `outbox.lock-seconds` | `60` |
| `outbox.cleanup-after-days` | `7` |

## Local Run

Start PostgreSQL, Redis and Kafka through the root compose file first:

```powershell
docker compose up -d
```

Run the service:

```powershell
.\gradlew.bat :authservice:bootRun
```

Run tests:

```powershell
.\gradlew.bat :authservice:test
```
