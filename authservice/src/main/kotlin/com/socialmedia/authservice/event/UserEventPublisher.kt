package com.socialmedia.authservice.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.socialmedia.authservice.messaging.OutboxEvent
import com.socialmedia.authservice.messaging.OutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserEventPublisher(
	private val outboxEventRepository: OutboxEventRepository,
	private val objectMapper: ObjectMapper,
) {
	private companion object {
		const val TOPIC = "social.users"

		val logger = LoggerFactory.getLogger(UserEventPublisher::class.java)
	}

	fun publishUserRegistered(event: UserRegisteredEvent) {
		runCatching {
			outboxEventRepository.save(
				OutboxEvent(
					id = event.eventId,
					aggregateId = event.userId.toString(),
					topic = TOPIC,
					eventType = "USER_REGISTERED",
					payload = objectMapper.writeValueAsString(event),
				),
			)
		}.onFailure { exception ->
			logger.warn("Failed to store user registered outbox event {}", event, exception)
		}
	}
}
