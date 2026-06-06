package com.socialmedia.userservice.repository

import com.socialmedia.userservice.model.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
	fun existsByUsername(username: String): Boolean

	fun findByUsernameContainingIgnoreCase(username: String, pageable: Pageable): Page<User>

	@Query(
		"""
		select u
		from User u
		where u.id in (
			select f.followerId
			from UserFollow f
			where f.followingId = :userId
		)
		""",
	)
	fun findFollowers(@Param("userId") userId: UUID, pageable: Pageable): Page<User>

	@Query(
		"""
		select u
		from User u
		where u.id in (
			select f.followingId
			from UserFollow f
			where f.followerId = :userId
		)
		""",
	)
	fun findFollowing(@Param("userId") userId: UUID, pageable: Pageable): Page<User>
}
