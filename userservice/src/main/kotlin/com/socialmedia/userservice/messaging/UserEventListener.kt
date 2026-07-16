package com.socialmedia.userservice.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.socialmedia.userservice.event.UserRegisteredEvent
import com.socialmedia.userservice.service.AutoUserProfileService
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserEventListener(
	private val objectMapper: ObjectMapper,
	private val autoUserProfileService: AutoUserProfileService,
	private val processedKafkaMessageRepository: ProcessedKafkaMessageRepository,
) {
	private companion object {
		const val TOPIC = "social.users"
		const val GROUP_ID = "userservice"

		val logger = LoggerFactory.getLogger(UserEventListener::class.java)
	}

	@KafkaListener(
		topics = ["\${users.kafka.topic:social.users}"],
		groupId = "\${users.kafka.group-id:userservice}",
	)
	@Transactional
	fun onMessage(payload: String) {
		runCatching {
			objectMapper.readValue(payload, UserRegisteredEvent::class.java)
		}.onSuccess { event ->
			val messageId = event.eventId.toString()

			if (processedKafkaMessageRepository.existsById(messageId)) {
				logger.debug("Skipping already processed Kafka message {}", messageId)
				return@onSuccess
			}

			autoUserProfileService.createProfile(event)
			try {
				processedKafkaMessageRepository.save(
					ProcessedKafkaMessage(
						messageId = messageId,
						topic = TOPIC,
						consumerGroup = GROUP_ID,
					),
				)
			} catch (exception: DataIntegrityViolationException) {
				logger.debug("Kafka message {} was processed concurrently", messageId)
			}
		}
			.onFailure { exception ->
				logger.warn("Failed to consume user event payload: {}", payload, exception)
			}
	}
}
