package com.socialmedia.postservice.model.response

import java.time.Instant
import java.util.UUID

data class PostCommentResponse(
	val id: UUID,
	val postId: UUID,
	val authorId: UUID,
	val content: String,
	val createdAt: Instant,
	val updatedAt: Instant,
)
