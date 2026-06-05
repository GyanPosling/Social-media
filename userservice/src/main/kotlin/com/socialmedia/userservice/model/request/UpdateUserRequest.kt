package com.socialmedia.userservice.model.request

import jakarta.validation.constraints.Size

data class UpdateUserRequest(
	@field:Size(min = 1, max = 100)
	val displayName: String? = null,

	@field:Size(max = 500)
	val bio: String? = null,

	@field:Size(max = 500)
	val avatarUrl: String? = null,
)
