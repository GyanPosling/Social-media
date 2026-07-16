package com.socialmedia.authservice.messaging

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "auth_outbox_events")
class OutboxEvent(
	@Id
	val id: UUID = UUID.randomUUID(),
	val aggregateId: String,
	val topic: String,
	val eventType: String,
	val payload: String,
	var status: String = STATUS_PENDING,
	var attempts: Int = 0,
	var lockedUntil: Instant? = null,
	var lastError: String? = null,
	val createdAt: Instant = Instant.now(),
	var publishedAt: Instant? = null,
) {
	companion object {
		const val STATUS_PENDING = "PENDING"
		const val STATUS_PUBLISHED = "PUBLISHED"
		const val STATUS_FAILED = "FAILED"
	}
}
