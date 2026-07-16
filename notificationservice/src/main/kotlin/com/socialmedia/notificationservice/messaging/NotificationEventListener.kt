package com.socialmedia.notificationservice.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.socialmedia.notificationservice.model.event.SocialNotificationEvent
import com.socialmedia.notificationservice.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NotificationEventListener(
	private val objectMapper: ObjectMapper,
	private val notificationService: NotificationService,
	private val processedKafkaMessageRepository: ProcessedKafkaMessageRepository,
) {
	private companion object {
		const val TOPIC = "social.notifications"
		const val GROUP_ID = "notificationservice"

		val logger = LoggerFactory.getLogger(NotificationEventListener::class.java)
	}

	@KafkaListener(
		topics = ["\${notifications.kafka.topic:social.notifications}"],
		groupId = "\${notifications.kafka.group-id:notificationservice}",
	)
	fun onMessage(payload: String) {
		runCatching {
			objectMapper.readValue(payload, SocialNotificationEvent::class.java)
		}.onSuccess { event ->
			val messageId = event.eventId.toString()

			if (processedKafkaMessageRepository.existsById(messageId)) {
				logger.debug("Skipping already processed Kafka message {}", messageId)
				return@onSuccess
			}

			notificationService.createNotification(event)
			try {
				processedKafkaMessageRepository.save(
					ProcessedKafkaMessage(
						messageId = messageId,
						topic = TOPIC,
						consumerGroup = GROUP_ID,
					),
				)
			} catch (exception: DuplicateKeyException) {
				logger.debug("Kafka message {} was processed concurrently", messageId)
			}
		}
			.onFailure { exception ->
				logger.warn("Failed to consume notification event payload: {}", payload, exception)
			}
	}
}
