package com.socialmedia.postservice.model.response

import java.time.Instant
import java.util.UUID

data class PostLikeResponse(
	val postId: UUID,
	val userId: UUID,
	val createdAt: Instant,
)
