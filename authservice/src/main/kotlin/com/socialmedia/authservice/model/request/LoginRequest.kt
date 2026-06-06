package com.socialmedia.authservice.model.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
	@field:NotBlank
	@field:Email
	@field:Size(max = 254)
	val email: String,

	@field:NotBlank
	@field:Size(min = 8, max = 72)
	val password: String,
)
