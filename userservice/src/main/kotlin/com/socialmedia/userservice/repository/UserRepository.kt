package com.socialmedia.userservice.repository

import com.socialmedia.userservice.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
	fun existsByUsername(username: String): Boolean

	fun findByUsernameContainingIgnoreCase(username: String): List<User>
}
