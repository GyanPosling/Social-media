package com.socialmedia.apigateway.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableConfigurationProperties(
	JwtProperties::class,
)
class SecurityConfig {
	@Bean
	fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
		http
			.csrf { it.disable() }
			.httpBasic { it.disable() }
			.formLogin { it.disable() }
			.authorizeExchange { it.anyExchange().permitAll() }
			.build()

	@Bean
	fun jwtDecoder(jwtProperties: JwtProperties): JwtDecoder {
		val secretBytes = jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
		require(secretBytes.size >= 32) { "security.jwt.secret must be at least 32 bytes" }

		val secretKey = SecretKeySpec(secretBytes, "HmacSHA256")

		return NimbusJwtDecoder
			.withSecretKey(secretKey)
			.macAlgorithm(MacAlgorithm.HS256)
			.build()
	}
}
