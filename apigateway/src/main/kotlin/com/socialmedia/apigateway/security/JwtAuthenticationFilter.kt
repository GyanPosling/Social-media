package com.socialmedia.apigateway.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class JwtAuthenticationFilter(
	private val jwtDecoder: JwtDecoder,
	@Value("\${gateway.internal-signing-secret}")
	private val internalSecret: String,
	@Value("\${security.auth.login-path:/auth/login}")
	private val loginPath: String,
	@Value("\${security.auth.register-path:/auth/register}")
	private val registerPath: String,
) : GlobalFilter, Ordered {
	private companion object {
		const val BEARER_PREFIX = "Bearer "
		const val SIGN_ALGORITHM = "HmacSHA256"
		const val USER_EMAIL_HEADER = "X-User-Email"
		const val TS_HEADER = "X-TS"
		const val SIGN_HEADER = "X-SIGN"

		val PUBLIC_PATH_PREFIXES = listOf(
			"/swagger-ui",
			"/swagger-ui.html",
			"/v3/api-docs",
			"/docs",
			"/actuator/health",
			"/actuator/info",
		)
	}

	override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
		val requestPath = exchange.request.path.value()

		if (shouldSkipAuth(requestPath)) {
			return chain.filter(exchange)
		}

		val token = extractBearerToken(exchange)
			?: return unauthorized()
		val jwt = decode(token)
			?: return unauthorized()
		val userId = jwt.subject
			?.let { parseUserId(it) }
			?: return unauthorized()
		val timestamp = Instant.now().epochSecond.toString()
		val signature = sign(
			payload = listOf(
				exchange.request.method.name(),
				requestPath,
				userId,
				jwt.getClaimAsString("email").orEmpty(),
				timestamp,
			).joinToString("|"),
		)

		val mutatedRequest = exchange.request.mutate()
			.headers {
				it.remove(CurrentUserHeader.NAME)
				it.remove(USER_EMAIL_HEADER)
				it.remove(TS_HEADER)
				it.remove(SIGN_HEADER)
			}
			.header(CurrentUserHeader.NAME, userId)
			.header(TS_HEADER, timestamp)
			.header(SIGN_HEADER, signature)
			.applyEmail(jwt)
			.build()

		return chain.filter(exchange.mutate().request(mutatedRequest).build())
	}

	override fun getOrder(): Int = -1

	private fun shouldSkipAuth(requestPath: String): Boolean =
		requestPath == loginPath ||
			requestPath == registerPath ||
			PUBLIC_PATH_PREFIXES.any(requestPath::startsWith)

	private fun extractBearerToken(exchange: ServerWebExchange): String? {
		val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)

		return authHeader
			?.takeIf { it.startsWith(BEARER_PREFIX) }
			?.substring(BEARER_PREFIX.length)
	}

	private fun decode(token: String): Jwt? =
		try {
			jwtDecoder.decode(token)
		} catch (exception: JwtException) {
			null
		}

	private fun parseUserId(subject: String): String? =
		try {
			UUID.fromString(subject).toString()
		} catch (exception: IllegalArgumentException) {
			null
		}

	private fun ServerHttpRequest.Builder.applyEmail(jwt: Jwt): ServerHttpRequest.Builder {
		val email = jwt.getClaimAsString("email")

		if (!email.isNullOrBlank()) {
			header(USER_EMAIL_HEADER, email)
		}

		return this
	}

	private fun sign(payload: String): String {
		if (internalSecret.isBlank()) {
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Gateway signing secret is missing")
		}

		val mac = Mac.getInstance(SIGN_ALGORITHM)
		mac.init(SecretKeySpec(internalSecret.toByteArray(StandardCharsets.UTF_8), SIGN_ALGORITHM))

		return Base64.getEncoder().encodeToString(mac.doFinal(payload.toByteArray(StandardCharsets.UTF_8)))
	}

	private fun unauthorized(): Mono<Void> =
		Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"))
}
