package com.socialmedia.authservice.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig {
	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
		http
			.csrf { it.disable() }
			.httpBasic { it.disable() }
			.formLogin { it.disable() }
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.authorizeHttpRequests {
				it
					.requestMatchers("/auth/**", "/actuator/health", "/actuator/info").permitAll()
					.anyRequest().authenticated()
			}
			.build()

	@Bean
	fun passwordEncoder(): PasswordEncoder =
		BCryptPasswordEncoder()

	@Bean
	fun jwtEncoder(jwtProperties: JwtProperties): JwtEncoder {
		val secretBytes = jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
		require(secretBytes.size >= 32) { "security.jwt.secret must be at least 32 bytes" }

		val secretKey = SecretKeySpec(secretBytes, "HmacSHA256")

		return NimbusJwtEncoder(ImmutableSecret<SecurityContext>(secretKey))
	}
}
