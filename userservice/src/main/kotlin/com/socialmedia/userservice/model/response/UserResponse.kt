package com.socialmedia.userservice.model.response

data class UserResponse(
	val id: Long,
	val username: String,
	val displayName: String,
	val avatarUrl: String?,
)
