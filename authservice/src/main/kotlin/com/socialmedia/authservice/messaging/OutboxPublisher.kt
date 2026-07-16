package com.socialmedia.authservice.messaging

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class OutboxPublisher(
	private val outboxEventRepository: OutboxEventRepository,
	private val kafkaTemplate: KafkaTemplate<String, String>,
	@Value("\${outbox.max-attempts:5}")
	private val maxAttempts: Int,
	@Value("\${outbox.lock-seconds:60}")
	private val lockSeconds: Long,
	@Value("\${outbox.cleanup-after-days:7}")
	private val cleanupAfterDays: Long,
) {
	private companion object {
		val logger = LoggerFactory.getLogger(OutboxPublisher::class.java)
	}

	@Transactional
	@Scheduled(fixedDelayString = "\${outbox.publish-delay-ms:5000}")
	fun publishPending() {
		val now = Instant.now()
		outboxEventRepository.findPublishable(OutboxEvent.STATUS_PENDING, maxAttempts, now)
			.forEach { event ->
				event.lockedUntil = now.plusSeconds(lockSeconds)
				event.attempts += 1
				runCatching {
					kafkaTemplate.send(event.topic, event.aggregateId, event.payload).get()
					event.status = OutboxEvent.STATUS_PUBLISHED
					event.publishedAt = Instant.now()
					event.lockedUntil = null
					event.lastError = null
				}.onFailure { exception ->
					event.lastError = exception.message?.take(1000)
					event.lockedUntil = null
					if (event.attempts >= maxAttempts) {
						kafkaTemplate.send("${event.topic}.DLT", event.aggregateId, event.payload).get()
						event.status = OutboxEvent.STATUS_FAILED
					}
					logger.warn("Failed to publish outbox event {}", event.id, exception)
				}
			}
	}

	@Transactional
	@Scheduled(cron = "\${outbox.cleanup-cron:0 0 3 * * *}")
	fun cleanupPublished() {
		outboxEventRepository.deleteByStatusAndPublishedAtBefore(
			OutboxEvent.STATUS_PUBLISHED,
			Instant.now().minusSeconds(cleanupAfterDays * 24 * 60 * 60),
		)
	}
}
