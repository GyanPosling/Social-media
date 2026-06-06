package com.socialmedia.postservice.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "posts")
class Post(
	@Id
	@Column(nullable = false)
	val id: UUID = UUID.randomUUID(),

	@Column(name = "author_id", nullable = false)
	val authorId: UUID,

	@Column(nullable = false, columnDefinition = "TEXT")
	var content: String,

	@Column(name = "image_url", length = 500)
	var imageUrl: String? = null,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant = Instant.now(),

	@Column(name = "updated_at", nullable = false)
	var updatedAt: Instant = Instant.now(),

	@Version
	@Column(nullable = false)
	var version: Long = 0,
)
