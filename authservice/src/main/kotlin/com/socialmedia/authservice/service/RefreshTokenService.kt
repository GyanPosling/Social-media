package com.socialmedia.authservice.service

import com.socialmedia.authservice.config.JwtProperties
import com.socialmedia.authservice.model.entity.AuthAccount
import com.socialmedia.authservice.model.entity.RefreshToken
import com.socialmedia.authservice.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64

@Service
class RefreshTokenService(
	private val refreshTokenRepository: RefreshTokenRepository,
	private val jwtProperties: JwtProperties,
) {
	private val secureRandom = SecureRandom()

	fun issue(account: AuthAccount): String {
		val rawToken = randomToken()
		refreshTokenRepository.save(
			RefreshToken(
				accountId = account.id,
				tokenHash = hash(rawToken),
				expiresAt = Instant.now().plus(jwtProperties.refreshTokenTtl),
			),
		)

		return rawToken
	}

	fun consume(rawToken: String): RefreshToken? {
		val refreshToken = refreshTokenRepository.findByTokenHash(hash(rawToken))
			?.takeIf(RefreshToken::active)
			?: return null

		refreshToken.revokedAt = Instant.now()

		return refreshToken
	}

	fun revoke(rawToken: String): Boolean {
		val refreshToken = refreshTokenRepository.findByTokenHash(hash(rawToken))
			?: return false

		if (refreshToken.revokedAt == null) {
			refreshToken.revokedAt = Instant.now()
		}

		return true
	}

	private fun randomToken(): String {
		val bytes = ByteArray(48)
		secureRandom.nextBytes(bytes)

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
	}

	private fun hash(rawToken: String): String {
		val digest = MessageDigest.getInstance("SHA-256")
			.digest(rawToken.toByteArray(Charsets.UTF_8))

		return digest.joinToString("") { "%02x".format(it) }
	}
}
