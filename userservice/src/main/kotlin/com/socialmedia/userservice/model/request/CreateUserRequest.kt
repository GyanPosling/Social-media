package com.socialmedia.userservice.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequest(
	@field:NotBlank
	@field:Size(min = 3, max = 50)
	val username: String,

	@field:NotBlank
	@field:Size(min = 1, max = 100)
	val displayName: String,

	@field:Size(max = 500)
	val bio: String? = null,

	@field:Size(max = 500)
	val avatarUrl: String? = null,
)
