package com.socialmedia.userservice.model.entity

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
	name = "user_follows",
	uniqueConstraints = [
		UniqueConstraint(
			name = "uk_user_follows_follower_following",
			columnNames = ["follower_id", "following_id"],
		),
	],
)
class UserFollow(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0,

	@Column(name = "follower_id", nullable = false)
	val followerId: UUID,

	@Column(name = "following_id", nullable = false)
	val followingId: UUID,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant = Instant.now(),
)
