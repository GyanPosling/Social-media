# User Service

`userservice` owns user profiles and social graph data. It creates profiles, updates profile fields, searches users, manages follows, and publishes follow notifications.

## Responsibilities

- Store user profiles and follow relationships in PostgreSQL.
- Create a default profile from `UserRegisteredEvent`.
- Support manual profile creation and profile updates.
- Search users by username.
- Return followers and following lists.
- Cache user/profile reads in Redis.
- Store follow notification events in `user_outbox_events` and publish them to Kafka.
- Consume registration events idempotently with `user_processed_kafka_messages`.

## Runtime

| Property | Value |
| --- | --- |
| Port | `8081` |
| Database | PostgreSQL |
| Cache | Redis, default TTL `10m` |
| Migrations | Flyway |
| Messaging | Kafka consumer + transactional outbox producer |
| Security | Gateway signature validation |

## API

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/users` | Create current user's profile |
| `GET` | `/users/{id}` | Get short user data |
| `GET` | `/users/{id}/profile` | Get full user profile |
| `PATCH` | `/users/me` | Update current user's profile |
| `GET` | `/users/search?query={text}` | Search by username |
| `POST` | `/users/{id}/follow` | Follow user |
| `DELETE` | `/users/{id}/follow` | Unfollow user |
| `GET` | `/users/{id}/followers` | List followers |
| `GET` | `/users/{id}/following` | List followed users |

Create profile request:

```json
{
  "username": "john_doe",
  "phone": "+375291234567",
  "displayName": "John Doe",
  "bio": "Backend developer",
  "avatarUrl": "https://example.com/avatar.png"
}
```

Update profile request:

```json
{
  "displayName": "John",
  "bio": "Updated bio",
  "avatarUrl": "https://example.com/new-avatar.png"
}
```

## Events

Consumes:

| Topic | Event | Action |
| --- | --- | --- |
| `social.users` | `UserRegisteredEvent` | Create default profile |

Produces:

| Topic | Type | When |
| --- | --- | --- |
| `social.notifications` | `USER_FOLLOWED` | A user follows another user |

The `social.users` consumer is idempotent. Notification events are published through outbox with retry limits, publish locks, cleanup, and dead-letter publishing to `social.notifications.DLT`.

## Cache

The service caches:

- short user response by user id
- full profile response by user id

Cache entries use Redis and expire after `10m` by default.

## Configuration

| Property | Default |
| --- | --- |
| `server.port` | `8081` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/social_media` |
| `spring.cache.redis.time-to-live` | `10m` |
| `spring.kafka.bootstrap-servers` | `localhost:9092` |
| `users.kafka.topic` | `social.users` |
| `users.kafka.group-id` | `userservice` |
| `gateway.internal-signing-secret` | `${GATEWAY_INTERNAL_SIGNING_SECRET:change-this-internal-secret-to-at-least-32-characters}` |
| `gateway.signature-max-age-seconds` | `300` |
| `outbox.max-attempts` | `5` |
| `outbox.lock-seconds` | `60` |
| `outbox.cleanup-after-days` | `7` |

## Local Run

```powershell
docker compose up -d
.\gradlew.bat :userservice:bootRun
```

Run tests:

```powershell
.\gradlew.bat :userservice:test
```
