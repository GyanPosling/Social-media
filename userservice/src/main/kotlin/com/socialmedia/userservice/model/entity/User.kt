package com.socialmedia.userservice.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(
	@Id
	@Column(nullable = false)
	val id: UUID,

	@Column(nullable = false, unique = true, length = 50)
	var username: String,

	@Column(length = 254)
	var email: String? = null,

	@Column(length = 32)
	var phone: String? = null,

	@Column(name = "display_name", nullable = false, length = 100)
	var displayName: String,

	@Column(length = 500)
	var bio: String? = null,

	@Column(name = "avatar_url", length = 500)
	var avatarUrl: String? = null,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant = Instant.now(),

	@Column(name = "updated_at", nullable = false)
	var updatedAt: Instant = Instant.now(),
)
