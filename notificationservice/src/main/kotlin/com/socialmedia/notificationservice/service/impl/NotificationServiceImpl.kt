package com.socialmedia.notificationservice.service.impl

import com.socialmedia.notificationservice.exception.NotificationNotFoundException
import com.socialmedia.notificationservice.mapper.NotificationMapper
import com.socialmedia.notificationservice.model.entity.Notification
import com.socialmedia.notificationservice.model.entity.NotificationType
import com.socialmedia.notificationservice.model.event.SocialNotificationEvent
import com.socialmedia.notificationservice.model.response.NotificationResponse
import com.socialmedia.notificationservice.repository.NotificationRepository
import com.socialmedia.notificationservice.service.NotificationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationServiceImpl(
	private val notificationRepository: NotificationRepository,
	private val notificationMapper: NotificationMapper,
) : NotificationService {
	override fun createNotification(event: SocialNotificationEvent) {
		val type = runCatching { NotificationType.valueOf(event.type) }.getOrNull() ?: return

		notificationRepository.save(
			Notification(
				recipientId = event.recipientId,
				actorId = event.actorId,
				type = type,
				postId = event.postId,
				commentId = event.commentId,
				createdAt = event.occurredAt,
			),
		)
	}

	override fun getNotifications(userId: UUID, pageable: Pageable): Page<NotificationResponse> =
		notificationRepository.findByRecipientId(userId, pageable)
			.map(notificationMapper::toResponse)

	override fun getUnreadCount(userId: UUID): Long =
		notificationRepository.countByRecipientIdAndReadFalse(userId)

	override fun markAsRead(userId: UUID, notificationId: String): NotificationResponse {
		val notification = notificationRepository.findByIdAndRecipientId(notificationId, userId)
			?: throw NotificationNotFoundException(notificationId)
		val readNotification = notificationRepository.save(notification.copy(read = true))

		return notificationMapper.toResponse(readNotification)
	}

	override fun markAllAsRead(userId: UUID) {
		val unreadNotifications = notificationRepository.findByRecipientIdAndReadFalse(userId)
			.map { it.copy(read = true) }

		if (unreadNotifications.isNotEmpty()) {
			notificationRepository.saveAll(unreadNotifications)
		}
	}
}
