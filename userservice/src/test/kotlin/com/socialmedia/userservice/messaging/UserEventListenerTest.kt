package com.socialmedia.userservice.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.socialmedia.userservice.event.UserRegisteredEvent
import com.socialmedia.userservice.service.AutoUserProfileService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.UUID

class UserEventListenerTest {
	private val objectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
	private val autoUserProfileService = Mockito.mock(AutoUserProfileService::class.java)
	private val processedKafkaMessageRepository = Mockito.mock(ProcessedKafkaMessageRepository::class.java)
	private val listener = UserEventListener(
		objectMapper = objectMapper,
		autoUserProfileService = autoUserProfileService,
		processedKafkaMessageRepository = processedKafkaMessageRepository,
	)

	@Test
	fun `onMessage creates profile and stores processed marker`() {
		val event = UserRegisteredEvent(
			userId = UUID.randomUUID(),
			email = "test@example.com",
		)

		Mockito.`when`(processedKafkaMessageRepository.existsById(event.eventId.toString()))
			.thenReturn(false)
		Mockito.`when`(processedKafkaMessageRepository.save(any(ProcessedKafkaMessage::class.java)))
			.thenAnswer { invocation -> invocation.arguments[0] }

		listener.onMessage(objectMapper.writeValueAsString(event))

		Mockito.verify(autoUserProfileService).createProfile(event)
		Mockito.verify(processedKafkaMessageRepository).save(any(ProcessedKafkaMessage::class.java))
	}

	@Test
	fun `onMessage skips already processed message`() {
		val event = UserRegisteredEvent(
			userId = UUID.randomUUID(),
			email = "test@example.com",
		)

		Mockito.`when`(processedKafkaMessageRepository.existsById(event.eventId.toString()))
			.thenReturn(true)

		listener.onMessage(objectMapper.writeValueAsString(event))

		Mockito.verifyNoInteractions(autoUserProfileService)
		Mockito.verify(processedKafkaMessageRepository, Mockito.never()).save(any(ProcessedKafkaMessage::class.java))
	}

	private fun <T> any(type: Class<T>): T = Mockito.any(type)
}
