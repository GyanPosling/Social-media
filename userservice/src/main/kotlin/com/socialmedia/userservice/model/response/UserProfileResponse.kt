package com.socialmedia.userservice.model.response

import java.time.Instant
import java.util.UUID

data class UserProfileResponse(
	val id: UUID,
	val username: String,
	val displayName: String,
	val bio: String?,
	val avatarUrl: String?,
	val followersCount: Long,
	val followingCount: Long,
	val createdAt: Instant,
)
