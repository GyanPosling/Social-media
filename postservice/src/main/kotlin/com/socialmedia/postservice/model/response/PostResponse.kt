package com.socialmedia.postservice.model.response

import java.time.Instant
import java.util.UUID

data class PostResponse(
	val id: UUID,
	val authorId: UUID,
	val content: String,
	val imageUrl: String?,
	val likesCount: Long,
	val commentsCount: Long,
	val createdAt: Instant,
	val updatedAt: Instant,
)
