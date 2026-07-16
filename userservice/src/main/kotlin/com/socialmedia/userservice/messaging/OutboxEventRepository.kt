package com.socialmedia.userservice.messaging

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.UUID
import jakarta.persistence.LockModeType
import java.time.Instant

interface OutboxEventRepository : JpaRepository<OutboxEvent, UUID> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(
		"""
		select e from OutboxEvent e
		where e.status = :status
		  and e.attempts < :maxAttempts
		  and (e.lockedUntil is null or e.lockedUntil < :now)
		order by e.createdAt asc
		limit 50
		""",
	)
	fun findPublishable(status: String, maxAttempts: Int, now: Instant): List<OutboxEvent>

	fun deleteByStatusAndPublishedAtBefore(status: String, publishedAt: Instant): Long
}
