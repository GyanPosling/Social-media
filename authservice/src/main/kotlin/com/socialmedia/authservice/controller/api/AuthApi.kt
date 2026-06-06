package com.socialmedia.authservice.controller.api

import com.socialmedia.authservice.model.request.LoginRequest
import com.socialmedia.authservice.model.request.RegisterRequest
import com.socialmedia.authservice.model.response.AuthResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/auth")
interface AuthApi {
	@PostMapping("/register")
	fun register(
		@Valid @RequestBody request: RegisterRequest,
	): AuthResponse

	@PostMapping("/login")
	fun login(
		@Valid @RequestBody request: LoginRequest,
	): AuthResponse
}
