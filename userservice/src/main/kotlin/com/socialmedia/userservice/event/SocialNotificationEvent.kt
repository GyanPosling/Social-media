package com.socialmedia.userservice.event

import java.time.Instant
import java.util.UUID

data class SocialNotificationEvent(
	val eventId: UUID = UUID.randomUUID(),
	val type: String,
	val recipientId: UUID,
	val actorId: UUID,
	val postId: UUID? = null,
	val commentId: UUID? = null,
	val occurredAt: Instant = Instant.now(),
)
