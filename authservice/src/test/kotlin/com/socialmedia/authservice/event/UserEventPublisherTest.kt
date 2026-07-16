package com.socialmedia.authservice.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.socialmedia.authservice.messaging.OutboxEvent
import com.socialmedia.authservice.messaging.OutboxEventRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import java.util.UUID

class UserEventPublisherTest {
	private val outboxEventRepository = Mockito.mock(OutboxEventRepository::class.java)
	private val objectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
	private val publisher = UserEventPublisher(outboxEventRepository, objectMapper)

	@Test
	fun `publishUserRegistered stores event in outbox`() {
		val event = UserRegisteredEvent(
			userId = UUID.randomUUID(),
			email = "test@example.com",
		)

		Mockito.`when`(outboxEventRepository.save(any(OutboxEvent::class.java)))
			.thenAnswer { invocation -> invocation.arguments[0] }

		publisher.publishUserRegistered(event)

		val captor = ArgumentCaptor.forClass(OutboxEvent::class.java)
		Mockito.verify(outboxEventRepository).save(captor.capture())
		val savedEvent = captor.value

		assertEquals(event.eventId, savedEvent.id)
		assertEquals(event.userId.toString(), savedEvent.aggregateId)
		assertEquals("social.users", savedEvent.topic)
		assertEquals("USER_REGISTERED", savedEvent.eventType)
		assertEquals(event, objectMapper.readValue(savedEvent.payload, UserRegisteredEvent::class.java))
	}

	private fun <T> any(type: Class<T>): T = Mockito.any(type)
}
