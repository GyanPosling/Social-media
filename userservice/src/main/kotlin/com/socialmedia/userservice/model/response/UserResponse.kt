package com.socialmedia.userservice.model.response

import java.util.UUID

data class UserResponse(
	val id: UUID,
	val username: String,
	val displayName: String,
	val avatarUrl: String?,
)
