package com.socialmedia.postservice.repository

import com.socialmedia.postservice.model.entity.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostRepository : JpaRepository<Post, UUID> {
	fun findByAuthorId(authorId: UUID, pageable: Pageable): Page<Post>
}
