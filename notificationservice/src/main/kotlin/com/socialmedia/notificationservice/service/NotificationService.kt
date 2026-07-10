package com.socialmedia.notificationservice.service

import com.socialmedia.notificationservice.model.event.SocialNotificationEvent
import com.socialmedia.notificationservice.model.response.NotificationResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface NotificationService {
	fun createNotification(event: SocialNotificationEvent)

	fun getNotifications(userId: UUID, pageable: Pageable): Page<NotificationResponse>

	fun getUnreadCount(userId: UUID): Long

	fun markAsRead(userId: UUID, notificationId: String): NotificationResponse

	fun markAllAsRead(userId: UUID)
}
