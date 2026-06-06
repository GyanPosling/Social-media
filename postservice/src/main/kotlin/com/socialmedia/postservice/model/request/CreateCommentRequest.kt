package com.socialmedia.postservice.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateCommentRequest(
	@field:NotBlank
	@field:Size(max = 2000)
	val content: String,
)
