package com.socialmedia.userservice.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateUserRequest(
	@field:NotBlank
	@field:Size(min = 3, max = 50)
	@field:Pattern(
		regexp = "^[a-zA-Z0-9_]+$",
		message = "must contain only letters, numbers and underscores",
	)
	val username: String,

	@field:Pattern(
		regexp = "^\\+?[0-9]{7,15}$",
		message = "must contain 7 to 15 digits and may start with +",
	)
	val phone: String? = null,

	@field:NotBlank
	@field:Size(min = 1, max = 100)
	val displayName: String,

	@field:Size(max = 500)
	val bio: String? = null,

	@field:Size(max = 500)
	@field:Pattern(
		regexp = "^https?://.+$",
		message = "must be a valid http or https URL",
	)
	val avatarUrl: String? = null,
)
