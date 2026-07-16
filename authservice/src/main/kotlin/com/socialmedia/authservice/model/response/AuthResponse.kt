package com.socialmedia.authservice.model.response

import java.time.Instant
import java.util.UUID

data class AuthResponse(
	val userId: UUID,
	val tokenType: String = "Bearer",
	val accessToken: String,
	val refreshToken: String,
	val expiresAt: Instant,
)
