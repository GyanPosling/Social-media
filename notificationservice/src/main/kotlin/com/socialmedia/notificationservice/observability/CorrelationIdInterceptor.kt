package com.socialmedia.notificationservice.observability

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.UUID

@Component
class CorrelationIdInterceptor : HandlerInterceptor {
	private companion object {
		const val HEADER = "X-Correlation-Id"
		const val MDC_KEY = "correlationId"
	}

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		val correlationId = request.getHeader(HEADER)
			?.takeIf(String::isNotBlank)
			?: UUID.randomUUID().toString()

		MDC.put(MDC_KEY, correlationId)
		response.setHeader(HEADER, correlationId)

		return true
	}

	override fun afterCompletion(
		request: HttpServletRequest,
		response: HttpServletResponse,
		handler: Any,
		ex: Exception?,
	) {
		MDC.remove(MDC_KEY)
	}
}
