package com.socialmedia.userservice.repository

import com.socialmedia.userservice.model.entity.UserFollow
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserFollowRepository : JpaRepository<UserFollow, Long> {
	fun existsByFollowerIdAndFollowingId(followerId: UUID, followingId: UUID): Boolean

	fun countByFollowerId(followerId: UUID): Long

	fun countByFollowingId(followingId: UUID): Long

	fun deleteByFollowerIdAndFollowingId(followerId: UUID, followingId: UUID)
}
