package com.socialmedia.postservice.repository

import com.socialmedia.postservice.model.entity.PostLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostLikeRepository : JpaRepository<PostLike, Long> {
	fun existsByPostIdAndUserId(postId: UUID, userId: UUID): Boolean

	fun countByPostId(postId: UUID): Long

	fun deleteByPostIdAndUserId(postId: UUID, userId: UUID)
}
