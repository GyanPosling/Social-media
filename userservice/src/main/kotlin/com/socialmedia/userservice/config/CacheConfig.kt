package com.socialmedia.userservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {
	@Bean
	fun redisCacheConfiguration(objectMapper: ObjectMapper): RedisCacheConfiguration =
		RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(10))
			.disableCachingNullValues()
			.serializeValuesWith(
				SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper)),
			)
}
