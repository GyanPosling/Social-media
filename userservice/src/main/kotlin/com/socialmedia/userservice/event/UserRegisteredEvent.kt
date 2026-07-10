package com.socialmedia.userservice.event

import java.time.Instant
import java.util.UUID

data class UserRegisteredEvent(
	val userId: UUID,
	val email: String,
	val occurredAt: Instant = Instant.now(),
)
