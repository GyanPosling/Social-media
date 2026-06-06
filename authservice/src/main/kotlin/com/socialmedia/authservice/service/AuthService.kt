package com.socialmedia.authservice.service

import com.socialmedia.authservice.model.request.LoginRequest
import com.socialmedia.authservice.model.request.RegisterRequest
import com.socialmedia.authservice.model.response.AuthResponse

interface AuthService {
	fun register(request: RegisterRequest): AuthResponse

	fun login(request: LoginRequest): AuthResponse
}
