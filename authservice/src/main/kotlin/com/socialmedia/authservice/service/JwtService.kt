package com.socialmedia.authservice.service

import com.socialmedia.authservice.config.JwtProperties
import com.socialmedia.authservice.model.entity.AuthAccount
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JwtService(
	private val jwtEncoder: JwtEncoder,
	private val jwtProperties: JwtProperties,
) {
	fun createAccessToken(account: AuthAccount): Token {
		val issuedAt = Instant.now()
		val expiresAt = issuedAt.plus(jwtProperties.accessTokenTtl)
		val claims = JwtClaimsSet.builder()
			.issuer("authservice")
			.issuedAt(issuedAt)
			.expiresAt(expiresAt)
			.subject(account.id.toString())
			.claim("email", account.email)
			.build()
		val headers = JwsHeader.with(MacAlgorithm.HS256).build()
		val tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).tokenValue

		return Token(
			value = tokenValue,
			expiresAt = expiresAt,
		)
	}

	data class Token(
		val value: String,
		val expiresAt: Instant,
	)
}
