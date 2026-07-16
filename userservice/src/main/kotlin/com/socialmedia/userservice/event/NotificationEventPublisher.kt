package com.socialmedia.userservice.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.socialmedia.userservice.messaging.OutboxEvent
import com.socialmedia.userservice.messaging.OutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationEventPublisher(
	private val outboxEventRepository: OutboxEventRepository,
	private val objectMapper: ObjectMapper,
) {
	private companion object {
		const val TOPIC = "social.notifications"

		val logger = LoggerFactory.getLogger(NotificationEventPublisher::class.java)
	}

	fun publish(event: SocialNotificationEvent) {
		runCatching {
			outboxEventRepository.save(
				OutboxEvent(
					id = event.eventId,
					aggregateId = event.recipientId.toString(),
					topic = TOPIC,
					eventType = event.type,
					payload = objectMapper.writeValueAsString(event),
				),
			)
		}.onFailure { exception ->
			logger.warn("Failed to store notification outbox event {}", event, exception)
		}
	}
}
