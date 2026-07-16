package com.socialmedia.userservice.messaging

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "user_processed_kafka_messages")
class ProcessedKafkaMessage(
	@Id
	val messageId: String,
	val topic: String,
	val consumerGroup: String,
	val processedAt: Instant = Instant.now(),
)
