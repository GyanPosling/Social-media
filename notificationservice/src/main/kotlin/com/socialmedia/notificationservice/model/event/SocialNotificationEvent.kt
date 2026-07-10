package com.socialmedia.notificationservice.model.event

import java.time.Instant
import java.util.UUID

data class SocialNotificationEvent(
	val type: String,
	val recipientId: UUID,
	val actorId: UUID,
	val postId: UUID? = null,
	val commentId: UUID? = null,
	val occurredAt: Instant = Instant.now(),
)
