package com.socialmedia.authservice.controller

import com.socialmedia.authservice.controller.api.AuthApi
import com.socialmedia.authservice.model.request.LoginRequest
import com.socialmedia.authservice.model.request.RegisterRequest
import com.socialmedia.authservice.model.response.AuthResponse
import com.socialmedia.authservice.service.AuthService
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
	private val authService: AuthService,
) : AuthApi {
	override fun register(request: RegisterRequest): AuthResponse =
		authService.register(request)

	override fun login(request: LoginRequest): AuthResponse =
		authService.login(request)
}
