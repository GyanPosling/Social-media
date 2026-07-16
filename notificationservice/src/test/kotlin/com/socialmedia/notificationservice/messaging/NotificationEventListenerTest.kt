package com.socialmedia.notificationservice.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.socialmedia.notificationservice.model.event.SocialNotificationEvent
import com.socialmedia.notificationservice.service.NotificationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.UUID

class NotificationEventListenerTest {
	private val objectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
	private val notificationService = Mockito.mock(NotificationService::class.java)
	private val processedKafkaMessageRepository = Mockito.mock(ProcessedKafkaMessageRepository::class.java)
	private val listener = NotificationEventListener(
		objectMapper = objectMapper,
		notificationService = notificationService,
		processedKafkaMessageRepository = processedKafkaMessageRepository,
	)

	@Test
	fun `onMessage creates notification and stores processed marker`() {
		val event = SocialNotificationEvent(
			type = "POST_LIKED",
			recipientId = UUID.randomUUID(),
			actorId = UUID.randomUUID(),
			postId = UUID.randomUUID(),
		)

		Mockito.`when`(processedKafkaMessageRepository.existsById(event.eventId.toString()))
			.thenReturn(false)
		Mockito.`when`(processedKafkaMessageRepository.save(any(ProcessedKafkaMessage::class.java)))
			.thenAnswer { invocation -> invocation.arguments[0] }

		listener.onMessage(objectMapper.writeValueAsString(event))

		Mockito.verify(notificationService).createNotification(event)
		Mockito.verify(processedKafkaMessageRepository).save(any(ProcessedKafkaMessage::class.java))
	}

	@Test
	fun `onMessage skips already processed message`() {
		val event = SocialNotificationEvent(
			type = "POST_LIKED",
			recipientId = UUID.randomUUID(),
			actorId = UUID.randomUUID(),
			postId = UUID.randomUUID(),
		)

		Mockito.`when`(processedKafkaMessageRepository.existsById(event.eventId.toString()))
			.thenReturn(true)

		listener.onMessage(objectMapper.writeValueAsString(event))

		Mockito.verifyNoInteractions(notificationService)
		Mockito.verify(processedKafkaMessageRepository, Mockito.never()).save(any(ProcessedKafkaMessage::class.java))
	}

	private fun <T> any(type: Class<T>): T = Mockito.any(type)
}
