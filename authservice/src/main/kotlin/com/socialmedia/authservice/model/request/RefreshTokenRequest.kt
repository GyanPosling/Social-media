package com.socialmedia.authservice.model.request

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
	@field:NotBlank
	val refreshToken: String,
)
