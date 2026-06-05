package com.socialmedia.userservice.repository

import com.socialmedia.userservice.model.entity.UserFollow
import org.springframework.data.jpa.repository.JpaRepository

interface UserFollowRepository : JpaRepository<UserFollow, Long> {
	fun existsByFollowerIdAndFollowingId(followerId: Long, followingId: Long): Boolean

	fun findByFollowerId(followerId: Long): List<UserFollow>

	fun findByFollowingId(followingId: Long): List<UserFollow>

	fun countByFollowerId(followerId: Long): Long

	fun countByFollowingId(followingId: Long): Long

	fun deleteByFollowerIdAndFollowingId(followerId: Long, followingId: Long)
}
