package com.socialmedia.authservice.service.impl

import com.socialmedia.authservice.event.UserEventPublisher
import com.socialmedia.authservice.event.UserRegisteredEvent
import com.socialmedia.authservice.exception.AuthAccountAlreadyExistsException
import com.socialmedia.authservice.exception.InvalidCredentialsException
import com.socialmedia.authservice.model.entity.AuthAccount
import com.socialmedia.authservice.model.request.LoginRequest
import com.socialmedia.authservice.model.request.LogoutRequest
import com.socialmedia.authservice.model.request.RefreshTokenRequest
import com.socialmedia.authservice.model.request.RegisterRequest
import com.socialmedia.authservice.model.response.AuthResponse
import com.socialmedia.authservice.repository.AuthAccountRepository
import com.socialmedia.authservice.service.AuthService
import com.socialmedia.authservice.service.AuditService
import com.socialmedia.authservice.service.JwtService
import com.socialmedia.authservice.service.RefreshTokenService
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
	private val refreshTokenService: RefreshTokenService,
	private val auditService: AuditService,
	private val userEventPublisher: UserEventPublisher,
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
		userEventPublisher.publishUserRegistered(
			UserRegisteredEvent(
				userId = savedAccount.id,
				email = savedAccount.email,
			),
		)
		auditService.record(savedAccount.id, "AUTH_REGISTERED", savedAccount.email)

		return savedAccount.toAuthResponse()
	}

	override fun login(request: LoginRequest): AuthResponse {
		val email = normalizeEmail(request.email)
		val account = authAccountRepository.findByEmail(email)
			?: throw InvalidCredentialsException()

		if (!passwordEncoder.matches(request.password, account.passwordHash)) {
			auditService.record(account.id, "AUTH_LOGIN_FAILED", account.email)
			throw InvalidCredentialsException()
		}

		auditService.record(account.id, "AUTH_LOGIN_SUCCEEDED", account.email)

		return account.toAuthResponse()
	}

	@Transactional
	override fun refresh(request: RefreshTokenRequest): AuthResponse {
		val refreshToken = refreshTokenService.consume(request.refreshToken)
			?: throw InvalidCredentialsException()
		val account = authAccountRepository.findById(refreshToken.accountId)
			.orElseThrow { InvalidCredentialsException() }

		auditService.record(account.id, "AUTH_TOKEN_REFRESHED", account.email)

		return account.toAuthResponse()
	}

	@Transactional
	override fun logout(request: LogoutRequest) {
		val revoked = refreshTokenService.revoke(request.refreshToken)

		auditService.record(null, if (revoked) "AUTH_LOGOUT" else "AUTH_LOGOUT_UNKNOWN_TOKEN")
	}

	private fun AuthAccount.toAuthResponse(): AuthResponse {
		val token = jwtService.createAccessToken(this)

		return AuthResponse(
			userId = id,
			accessToken = token.value,
			refreshToken = refreshTokenService.issue(this),
			expiresAt = token.expiresAt,
		)
	}

	private fun normalizeEmail(email: String): String =
		email.trim().lowercase()
}
