package com.socialmedia.userservice.model.response

import java.time.Instant

data class FollowResponse(
	val followerId: Long,
	val followingId: Long,
	val createdAt: Instant,
)
