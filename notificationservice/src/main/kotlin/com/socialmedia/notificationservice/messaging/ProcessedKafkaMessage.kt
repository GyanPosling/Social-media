package com.socialmedia.notificationservice.messaging

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("processed_kafka_messages")
data class ProcessedKafkaMessage(
	@Id
	val messageId: String,
	val topic: String,
	val consumerGroup: String,
	val processedAt: Instant = Instant.now(),
)
