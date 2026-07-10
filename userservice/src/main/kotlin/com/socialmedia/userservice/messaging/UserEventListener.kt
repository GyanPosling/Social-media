package com.socialmedia.userservice.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.socialmedia.userservice.event.UserRegisteredEvent
import com.socialmedia.userservice.service.AutoUserProfileService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserEventListener(
	private val objectMapper: ObjectMapper,
	private val autoUserProfileService: AutoUserProfileService,
) {
	private companion object {
		val logger = LoggerFactory.getLogger(UserEventListener::class.java)
	}

	@KafkaListener(
		topics = ["\${users.kafka.topic:social.users}"],
		groupId = "\${users.kafka.group-id:userservice}",
	)
	fun onMessage(payload: String) {
		runCatching {
			objectMapper.readValue(payload, UserRegisteredEvent::class.java)
		}.onSuccess(autoUserProfileService::createProfile)
			.onFailure { exception ->
				logger.warn("Failed to consume user event payload: {}", payload, exception)
			}
	}
}
