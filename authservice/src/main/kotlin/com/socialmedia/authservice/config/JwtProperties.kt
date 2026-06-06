package com.socialmedia.authservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "security.jwt")
data class JwtProperties(
	val secret: String,
	val accessTokenTtl: Duration,
)
