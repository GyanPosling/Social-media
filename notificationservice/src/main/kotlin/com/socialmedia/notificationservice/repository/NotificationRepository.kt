package com.socialmedia.notificationservice.repository

import com.socialmedia.notificationservice.model.entity.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface NotificationRepository : MongoRepository<Notification, String> {
	fun findByRecipientId(recipientId: UUID, pageable: Pageable): Page<Notification>

	fun findByIdAndRecipientId(id: String, recipientId: UUID): Notification?

	fun countByRecipientIdAndReadFalse(recipientId: UUID): Long

	fun findByRecipientIdAndReadFalse(recipientId: UUID): List<Notification>
}
