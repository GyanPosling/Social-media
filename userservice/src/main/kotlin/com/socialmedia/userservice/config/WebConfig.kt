package com.socialmedia.userservice.config

import com.socialmedia.userservice.security.GatewaySignatureInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
	private val gatewaySignatureInterceptor: GatewaySignatureInterceptor,
) : WebMvcConfigurer {
	@Bean
	fun pageableResolverCustomizer(): PageableHandlerMethodArgumentResolverCustomizer =
		PageableHandlerMethodArgumentResolverCustomizer { resolver ->
			resolver.setMaxPageSize(100)
		}

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(gatewaySignatureInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns(
				"/actuator/health",
				"/actuator/info",
				"/v3/api-docs/**",
				"/swagger-ui/**",
				"/swagger-ui.html",
			)
	}

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000", "http://localhost:5173", "http://localhost:8080")
			.allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
			.allowedHeaders("*")
			.allowCredentials(true)
	}
}
