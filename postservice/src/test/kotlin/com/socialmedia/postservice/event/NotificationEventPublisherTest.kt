package com.socialmedia.postservice.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.socialmedia.postservice.messaging.OutboxEvent
import com.socialmedia.postservice.messaging.OutboxEventRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import java.util.UUID

class NotificationEventPublisherTest {
	private val outboxEventRepository = Mockito.mock(OutboxEventRepository::class.java)
	private val objectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
	private val publisher = NotificationEventPublisher(outboxEventRepository, objectMapper)

	@Test
	fun `publish stores notification event in outbox`() {
		val event = SocialNotificationEvent(
			type = "POST_COMMENTED",
			recipientId = UUID.randomUUID(),
			actorId = UUID.randomUUID(),
			postId = UUID.randomUUID(),
			commentId = UUID.randomUUID(),
		)

		Mockito.`when`(outboxEventRepository.save(any(OutboxEvent::class.java)))
			.thenAnswer { invocation -> invocation.arguments[0] }

		publisher.publish(event)

		val captor = ArgumentCaptor.forClass(OutboxEvent::class.java)
		Mockito.verify(outboxEventRepository).save(captor.capture())
		val savedEvent = captor.value

		assertEquals(event.eventId, savedEvent.id)
		assertEquals(event.recipientId.toString(), savedEvent.aggregateId)
		assertEquals("social.notifications", savedEvent.topic)
		assertEquals(event.type, savedEvent.eventType)
		assertEquals(event, objectMapper.readValue(savedEvent.payload, SocialNotificationEvent::class.java))
	}

	private fun <T> any(type: Class<T>): T = Mockito.any(type)
}
