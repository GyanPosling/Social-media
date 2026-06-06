package com.socialmedia.userservice.model.response

import java.time.Instant
import java.util.UUID

data class FollowResponse(
	val followerId: UUID,
	val followingId: UUID,
	val createdAt: Instant,
)
