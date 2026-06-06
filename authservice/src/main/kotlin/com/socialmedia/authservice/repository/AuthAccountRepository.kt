package com.socialmedia.authservice.repository

import com.socialmedia.authservice.model.entity.AuthAccount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AuthAccountRepository : JpaRepository<AuthAccount, UUID> {
	fun existsByEmail(email: String): Boolean

	fun findByEmail(email: String): AuthAccount?
}
