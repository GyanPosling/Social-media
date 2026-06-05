package com.socialmedia.userservice.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
	@Bean
	fun userserviceOpenApi(): OpenAPI =
		OpenAPI()
			.info(
				Info()
					.title("User Service API")
					.description("User profiles, search and follows API")
					.version("v1"),
			)
}
