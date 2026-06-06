package com.socialmedia.userservice.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs

@Component
class GatewaySignatureInterceptor(
	@Value("\${gateway.internal-signing-secret}")
	private val internalSecret: String,
	@Value("\${gateway.signature-max-age-seconds:300}")
	private val signatureMaxAgeSeconds: Long,
) : HandlerInterceptor {
	private companion object {
		const val SIGN_ALGORITHM = "HmacSHA256"
	}

	override fun preHandle(
		request: HttpServletRequest,
		response: HttpServletResponse,
		handler: Any,
	): Boolean {
		if (isPublicPath(request.requestURI) || request.method.equals("OPTIONS", ignoreCase = true)) {
			return true
		}

		val userId = request.getHeader(CurrentUserHeader.NAME)
		val email = request.getHeader(GatewayHeader.USER_EMAIL).orEmpty()
		val timestamp = request.getHeader(GatewayHeader.TIMESTAMP)
		val signature = request.getHeader(GatewayHeader.SIGNATURE)

		if (userId.isNullOrBlank() || timestamp.isNullOrBlank() || signature.isNullOrBlank()) {
			response.sendUnauthorized()
			return false
		}

		if (!isValidUuid(userId) || !isFresh(timestamp)) {
			response.sendUnauthorized()
			return false
		}

		val payload = listOf(
			request.method,
			request.requestURI,
			userId,
			email,
			timestamp,
		).joinToString("|")
		val expectedSignature = sign(payload)

		if (!constantTimeEquals(expectedSignature, signature)) {
			response.sendUnauthorized()
			return false
		}

		return true
	}

	private fun isPublicPath(path: String): Boolean =
		path.startsWith("/actuator/health") ||
			path.startsWith("/actuator/info") ||
			path.startsWith("/v3/api-docs") ||
			path.startsWith("/swagger-ui")

	private fun isValidUuid(value: String): Boolean =
		try {
			UUID.fromString(value)
			true
		} catch (exception: IllegalArgumentException) {
			false
		}

	private fun isFresh(timestamp: String): Boolean {
		val requestEpochSecond = timestamp.toLongOrNull() ?: return false
		val nowEpochSecond = Instant.now().epochSecond

		return abs(nowEpochSecond - requestEpochSecond) <= signatureMaxAgeSeconds
	}

	private fun sign(payload: String): String {
		val mac = Mac.getInstance(SIGN_ALGORITHM)
		mac.init(SecretKeySpec(internalSecret.toByteArray(StandardCharsets.UTF_8), SIGN_ALGORITHM))

		return Base64.getEncoder().encodeToString(mac.doFinal(payload.toByteArray(StandardCharsets.UTF_8)))
	}

	private fun constantTimeEquals(expected: String, actual: String): Boolean =
		MessageDigest.isEqual(
			expected.toByteArray(StandardCharsets.UTF_8),
			actual.toByteArray(StandardCharsets.UTF_8),
		)

	private fun HttpServletResponse.sendUnauthorized() {
		sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid gateway signature")
	}
}
