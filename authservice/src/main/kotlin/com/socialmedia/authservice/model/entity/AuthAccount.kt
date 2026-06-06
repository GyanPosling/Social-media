package com.socialmedia.authservice.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "auth_accounts")
class AuthAccount(
	@Id
	@Column(nullable = false)
	val id: UUID = UUID.randomUUID(),

	@Column(nullable = false, unique = true, length = 254)
	val email: String,

	@Column(name = "password_hash", nullable = false, length = 100)
	val passwordHash: String,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant = Instant.now(),

	@Column(name = "updated_at", nullable = false)
	var updatedAt: Instant = Instant.now(),
)
