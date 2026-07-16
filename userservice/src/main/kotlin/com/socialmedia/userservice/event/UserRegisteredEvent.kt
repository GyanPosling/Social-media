package com.socialmedia.userservice.event

import java.time.Instant
import java.util.UUID

data class UserRegisteredEvent(
	val eventId: UUID = UUID.randomUUID(),
	val userId: UUID,
	val email: String,
	val occurredAt: Instant = Instant.now(),
)
