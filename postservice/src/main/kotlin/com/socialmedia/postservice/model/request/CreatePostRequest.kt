package com.socialmedia.postservice.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreatePostRequest(
	@field:NotBlank
	@field:Size(max = 5000)
	val content: String,

	@field:Size(max = 500)
	@field:Pattern(
		regexp = "^https?://.+$",
		message = "must be a valid http or https URL",
	)
	val imageUrl: String? = null,
)
