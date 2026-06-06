package com.socialmedia.postservice.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant
import java.util.UUID

@Entity
@Table(
	name = "post_likes",
	uniqueConstraints = [
		UniqueConstraint(
			name = "uk_post_likes_post_user",
			columnNames = ["post_id", "user_id"],
		),
	],
)
class PostLike(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0,

	@Column(name = "post_id", nullable = false)
	val postId: UUID,

	@Column(name = "user_id", nullable = false)
	val userId: UUID,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant = Instant.now(),
)
