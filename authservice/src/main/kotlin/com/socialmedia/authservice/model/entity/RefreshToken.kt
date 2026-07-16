package com.socialmedia.authservice.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
	@Id
	val id: UUID = UUID.randomUUID(),

	@Column(name = "account_id", nullable = false)
	val accountId: UUID,

	@Column(name = "token_hash", nullable = false, unique = true, length = 64)
	val tokenHash: String,

	@Column(name = "expires_at", nullable = false)
	val expiresAt: Instant,

	@Column(name = "revoked_at")
	var revokedAt: Instant? = null,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant = Instant.now(),
) {
	val active: Boolean
		get() = revokedAt == null && expiresAt.isAfter(Instant.now())
}
