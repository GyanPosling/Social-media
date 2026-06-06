package com.socialmedia.authservice.service.impl

import com.socialmedia.authservice.exception.AuthAccountAlreadyExistsException
import com.socialmedia.authservice.exception.InvalidCredentialsException
import com.socialmedia.authservice.model.entity.AuthAccount
import com.socialmedia.authservice.model.request.LoginRequest
import com.socialmedia.authservice.model.request.RegisterRequest
import com.socialmedia.authservice.model.response.AuthResponse
import com.socialmedia.authservice.repository.AuthAccountRepository
import com.socialmedia.authservice.service.AuthService
import com.socialmedia.authservice.service.JwtService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthServiceImpl(
	private val authAccountRepository: AuthAccountRepository,
	private val passwordEncoder: PasswordEncoder,
	private val jwtService: JwtService,
) : AuthService {
	@Transactional
	override fun register(request: RegisterRequest): AuthResponse {
		val email = normalizeEmail(request.email)

		if (authAccountRepository.existsByEmail(email)) {
			throw AuthAccountAlreadyExistsException(email)
		}

		val account = AuthAccount(
			email = email,
			passwordHash = passwordEncoder.encode(request.password),
		)
		val savedAccount = try {
			authAccountRepository.save(account)
		} catch (exception: DataIntegrityViolationException) {
			throw AuthAccountAlreadyExistsException(email)
		}

		return savedAccount.toAuthResponse()
	}

	override fun login(request: LoginRequest): AuthResponse {
		val email = normalizeEmail(request.email)
		val account = authAccountRepository.findByEmail(email)
			?: throw InvalidCredentialsException()

		if (!passwordEncoder.matches(request.password, account.passwordHash)) {
			throw InvalidCredentialsException()
		}

		return account.toAuthResponse()
	}

	private fun AuthAccount.toAuthResponse(): AuthResponse {
		val token = jwtService.createAccessToken(this)

		return AuthResponse(
			userId = id,
			accessToken = token.value,
			expiresAt = token.expiresAt,
		)
	}

	private fun normalizeEmail(email: String): String =
		email.trim().lowercase()
}
