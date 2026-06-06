package com.socialmedia.userservice.model.request

import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Pattern

data class UpdateUserRequest(
	@field:Size(min = 1, max = 100)
	val displayName: String? = null,

	@field:Size(max = 500)
	val bio: String? = null,

	@field:Size(max = 500)
	@field:Pattern(
		regexp = "^https?://.+$",
		message = "must be a valid http or https URL",
	)
	val avatarUrl: String? = null,
)
