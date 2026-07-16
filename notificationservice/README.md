# Notification Service

`notificationservice` stores notification records and exposes read APIs for the current user. It consumes social notification events from Kafka and persists them in MongoDB.

## Responsibilities

- Consume `social.notifications` Kafka events.
- Skip already processed Kafka messages through `processed_kafka_messages`.
- Store notification records in MongoDB.
- Return paged notifications for the current user.
- Return unread notification count.
- Mark one notification as read.
- Mark all current user's notifications as read.

## Runtime

| Property | Value |
| --- | --- |
| Port | `8084` |
| Database | MongoDB |
| Messaging | Kafka consumer |
| Security | Gateway signature validation |

## API

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/notifications` | Get current user's notifications |
| `GET` | `/notifications/unread-count` | Get unread count |
| `PATCH` | `/notifications/{id}/read` | Mark notification as read |
| `PATCH` | `/notifications/read-all` | Mark all notifications as read |

Notification response:

```json
{
  "id": "notification-id",
  "recipientId": "00000000-0000-0000-0000-000000000000",
  "actorId": "11111111-1111-1111-1111-111111111111",
  "type": "POST_COMMENTED",
  "postId": "22222222-2222-2222-2222-222222222222",
  "commentId": "33333333-3333-3333-3333-333333333333",
  "read": false,
  "createdAt": "2026-01-01T12:00:00Z"
}
```

Supported notification types:

- `USER_FOLLOWED`
- `POST_LIKED`
- `POST_COMMENTED`

## Events

Consumes:

| Topic | Event |
| --- | --- |
| `social.notifications` | `SocialNotificationEvent` |

Invalid notification types are ignored by the service.

Kafka messages are idempotent by `eventId`, so duplicate deliveries do not create duplicate notification records.

## Configuration

| Property | Default |
| --- | --- |
| `server.port` | `8084` |
| `spring.data.mongodb.uri` | `mongodb://root:secret@localhost:27017/notifications?authSource=admin` |
| `spring.kafka.bootstrap-servers` | `localhost:9092` |
| `notifications.kafka.topic` | `social.notifications` |
| `notifications.kafka.group-id` | `notificationservice` |
| `gateway.internal-signing-secret` | `${GATEWAY_INTERNAL_SIGNING_SECRET:change-this-internal-secret-to-at-least-32-characters}` |
| `gateway.signature-max-age-seconds` | `300` |

## Local Run

```powershell
docker compose up -d
.\gradlew.bat :notificationservice:bootRun
```

Run tests:

```powershell
.\gradlew.bat :notificationservice:test
```
