package com.socialmedia.authservice.repository

import com.socialmedia.authservice.model.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
	fun findByTokenHash(tokenHash: String): RefreshToken?

	fun deleteByExpiresAtBefore(expiresAt: Instant): Long
}
