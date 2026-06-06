package com.socialmedia.apigateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig(
	@Value("\${cors.allowed-origins:http://localhost:3000}")
	private val allowedOrigins: String,
) {
	@Bean
	fun corsWebFilter(): CorsWebFilter {
		val configuration = CorsConfiguration().apply {
			this.allowedOrigins = allowedOrigins.split(",").map(String::trim)
			allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
			allowedHeaders = listOf("Authorization", "Content-Type", "Cache-Control")
			exposedHeaders = listOf("Authorization")
		}
		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/**", configuration)

		return CorsWebFilter(source)
	}
}
