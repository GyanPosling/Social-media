package com.socialmedia.authservice.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "audit_events")
class AuditEvent(
	@Id
	val id: UUID = UUID.randomUUID(),

	@Column(name = "account_id")
	val accountId: UUID?,

	@Column(name = "event_type", nullable = false, length = 120)
	val eventType: String,

	@Column(length = 500)
	val details: String? = null,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant = Instant.now(),
)
