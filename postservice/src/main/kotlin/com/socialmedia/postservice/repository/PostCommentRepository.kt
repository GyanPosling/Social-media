package com.socialmedia.postservice.repository

import com.socialmedia.postservice.model.entity.PostComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostCommentRepository : JpaRepository<PostComment, UUID> {
	fun findByPostId(postId: UUID, pageable: Pageable): Page<PostComment>

	fun countByPostId(postId: UUID): Long
}
