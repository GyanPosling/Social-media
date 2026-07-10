package com.socialmedia.notificationservice.model.response

import com.socialmedia.notificationservice.model.entity.NotificationType
import java.time.Instant
import java.util.UUID

data class NotificationResponse(
	val id: String,
	val recipientId: UUID,
	val actorId: UUID,
	val type: NotificationType,
	val postId: UUID?,
	val commentId: UUID?,
	val read: Boolean,
	val createdAt: Instant,
)
