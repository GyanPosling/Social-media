package com.socialmedia.notificationservice.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document(collection = "notifications")
data class Notification(
	@Id
	val id: String? = null,

	@Indexed
	val recipientId: UUID,

	val actorId: UUID,

	val type: NotificationType,

	val postId: UUID? = null,

	val commentId: UUID? = null,

	val read: Boolean = false,

	val createdAt: Instant = Instant.now(),
)
