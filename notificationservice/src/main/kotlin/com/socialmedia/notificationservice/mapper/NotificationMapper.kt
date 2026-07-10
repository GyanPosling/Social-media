package com.socialmedia.notificationservice.mapper

import com.socialmedia.notificationservice.model.entity.Notification
import com.socialmedia.notificationservice.model.response.NotificationResponse
import org.springframework.stereotype.Component

@Component
class NotificationMapper {
	fun toResponse(notification: Notification): NotificationResponse =
		NotificationResponse(
			id = requireNotNull(notification.id),
			recipientId = notification.recipientId,
			actorId = notification.actorId,
			type = notification.type,
			postId = notification.postId,
			commentId = notification.commentId,
			read = notification.read,
			createdAt = notification.createdAt,
		)
}
