package com.socialmedia.apigateway.observability

import org.slf4j.MDC
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class CorrelationIdFilter : GlobalFilter, Ordered {
	private companion object {
		const val HEADER = "X-Correlation-Id"
		const val MDC_KEY = "correlationId"
	}

	override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
		val correlationId = exchange.request.headers.getFirst(HEADER)
			?.takeIf(String::isNotBlank)
			?: UUID.randomUUID().toString()
		val request = exchange.request.mutate()
			.header(HEADER, correlationId)
			.build()
		val mutatedExchange = exchange.mutate()
			.request(request)
			.build()

		mutatedExchange.response.headers.set(HEADER, correlationId)
		MDC.put(MDC_KEY, correlationId)

		return chain.filter(mutatedExchange)
			.doFinally { MDC.remove(MDC_KEY) }
	}

	override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}
