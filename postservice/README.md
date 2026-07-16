# Post Service

`postservice` owns posts and post interactions. It handles post CRUD, feed reads, likes, comments, and publishes notification events for likes and comments.

## Responsibilities

- Store posts, likes, and comments in PostgreSQL.
- Return a global feed ordered by creation time.
- Return posts by author.
- Enforce ownership for post updates and deletes.
- Prevent duplicate likes through repository checks and database constraints.
- Store notification events in `post_outbox_events` and publish them to Kafka.

## Runtime

| Property | Value |
| --- | --- |
| Port | `8082` |
| Database | PostgreSQL |
| Migrations | Flyway, table `flyway_schema_history_post` |
| Messaging | Transactional outbox Kafka producer |
| Security | Gateway signature validation |

## API

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/posts` | Create post |
| `GET` | `/posts/{id}` | Get post |
| `PATCH` | `/posts/{id}` | Update own post |
| `DELETE` | `/posts/{id}` | Delete own post |
| `GET` | `/posts/users/{userId}` | Get user's posts |
| `GET` | `/posts/feed` | Get feed |
| `POST` | `/posts/{id}/likes` | Like post |
| `DELETE` | `/posts/{id}/likes` | Unlike post |
| `POST` | `/posts/{id}/comments` | Add comment |
| `GET` | `/posts/{id}/comments` | Get comments |
| `DELETE` | `/posts/comments/{commentId}` | Delete own comment |

Create post request:

```json
{
  "content": "First post",
  "imageUrl": "https://example.com/image.png"
}
```

Update post request:

```json
{
  "content": "Updated content",
  "imageUrl": null
}
```

Create comment request:

```json
{
  "content": "Nice post"
}
```

## Events

Produces:

| Topic | Type | When |
| --- | --- | --- |
| `social.notifications` | `POST_LIKED` | A user likes someone else's post |
| `social.notifications` | `POST_COMMENTED` | A user comments on someone else's post |

Notification events are published through outbox with retry limits, publish locks, cleanup, and dead-letter publishing to `social.notifications.DLT`.

## Configuration

| Property | Default |
| --- | --- |
| `server.port` | `8082` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/social_media` |
| `spring.kafka.bootstrap-servers` | `localhost:9092` |
| `gateway.internal-signing-secret` | `${GATEWAY_INTERNAL_SIGNING_SECRET:change-this-internal-secret-to-at-least-32-characters}` |
| `gateway.signature-max-age-seconds` | `300` |
| `outbox.max-attempts` | `5` |
| `outbox.lock-seconds` | `60` |
| `outbox.cleanup-after-days` | `7` |

## Local Run

```powershell
docker compose up -d
.\gradlew.bat :postservice:bootRun
```

Run tests:

```powershell
.\gradlew.bat :postservice:test
```
