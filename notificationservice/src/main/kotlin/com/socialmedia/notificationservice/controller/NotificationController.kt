package com.socialmedia.notificationservice.controller

import com.socialmedia.notificationservice.controller.api.NotificationApi
import com.socialmedia.notificationservice.model.response.NotificationResponse
import com.socialmedia.notificationservice.service.NotificationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class NotificationController(
	private val notificationService: NotificationService,
) : NotificationApi {
	override fun getNotifications(userId: UUID, pageable: Pageable): Page<NotificationResponse> =
		notificationService.getNotifications(userId, pageable)

	override fun getUnreadCount(userId: UUID): Map<String, Long> =
		mapOf("unreadCount" to notificationService.getUnreadCount(userId))

	override fun markAsRead(userId: UUID, id: String): NotificationResponse =
		notificationService.markAsRead(userId, id)

	override fun markAllAsRead(userId: UUID): ResponseEntity<Void> {
		notificationService.markAllAsRead(userId)

		return ResponseEntity.noContent().build()
	}
}
