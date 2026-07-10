package com.socialmedia.authservice.event

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class UserEventPublisher(
	private val kafkaTemplate: KafkaTemplate<String, String>,
	private val objectMapper: ObjectMapper,
) {
	private companion object {
		const val TOPIC = "social.users"

		val logger = LoggerFactory.getLogger(UserEventPublisher::class.java)
	}

	fun publishUserRegistered(event: UserRegisteredEvent) {
		runCatching {
			kafkaTemplate.send(TOPIC, event.userId.toString(), objectMapper.writeValueAsString(event))
		}.onFailure { exception ->
			logger.warn("Failed to publish user registered event {}", event, exception)
		}
	}
}
