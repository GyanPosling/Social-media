package com.socialmedia.postservice.event

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class NotificationEventPublisher(
	private val kafkaTemplate: KafkaTemplate<String, String>,
	private val objectMapper: ObjectMapper,
) {
	private companion object {
		const val TOPIC = "social.notifications"

		val logger = LoggerFactory.getLogger(NotificationEventPublisher::class.java)
	}

	fun publish(event: SocialNotificationEvent) {
		runCatching {
			kafkaTemplate.send(TOPIC, event.recipientId.toString(), objectMapper.writeValueAsString(event))
		}.onFailure { exception ->
			logger.warn("Failed to publish notification event {}", event, exception)
		}
	}
}
