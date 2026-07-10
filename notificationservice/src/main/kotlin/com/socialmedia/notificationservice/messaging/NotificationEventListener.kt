package com.socialmedia.notificationservice.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.socialmedia.notificationservice.model.event.SocialNotificationEvent
import com.socialmedia.notificationservice.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NotificationEventListener(
	private val objectMapper: ObjectMapper,
	private val notificationService: NotificationService,
) {
	private companion object {
		val logger = LoggerFactory.getLogger(NotificationEventListener::class.java)
	}

	@KafkaListener(
		topics = ["\${notifications.kafka.topic:social.notifications}"],
		groupId = "\${notifications.kafka.group-id:notificationservice}",
	)
	fun onMessage(payload: String) {
		runCatching {
			objectMapper.readValue(payload, SocialNotificationEvent::class.java)
		}.onSuccess(notificationService::createNotification)
			.onFailure { exception ->
				logger.warn("Failed to consume notification event payload: {}", payload, exception)
			}
	}
}
