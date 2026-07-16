package com.socialmedia.authservice.service

import com.socialmedia.authservice.model.entity.AuditEvent
import com.socialmedia.authservice.repository.AuditEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuditService(
	private val auditEventRepository: AuditEventRepository,
) {
	private companion object {
		val logger = LoggerFactory.getLogger(AuditService::class.java)
	}

	fun record(accountId: UUID?, eventType: String, details: String? = null) {
		runCatching {
			auditEventRepository.save(
				AuditEvent(
					accountId = accountId,
					eventType = eventType,
					details = details?.take(500),
				),
			)
		}.onFailure { exception ->
			logger.warn("Failed to write audit event {}", eventType, exception)
		}
	}
}
