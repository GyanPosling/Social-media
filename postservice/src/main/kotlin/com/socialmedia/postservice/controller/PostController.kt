package com.socialmedia.postservice.controller

import com.socialmedia.postservice.controller.api.PostApi
import com.socialmedia.postservice.model.request.CreateCommentRequest
import com.socialmedia.postservice.model.request.CreatePostRequest
import com.socialmedia.postservice.model.request.UpdatePostRequest
import com.socialmedia.postservice.model.response.PostCommentResponse
import com.socialmedia.postservice.model.response.PostLikeResponse
import com.socialmedia.postservice.model.response.PostResponse
import com.socialmedia.postservice.service.PostService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class PostController(
	private val postService: PostService,
) : PostApi {
	override fun createPost(authorId: UUID, request: CreatePostRequest): ResponseEntity<PostResponse> {
		val post = postService.createPost(authorId, request)

		return ResponseEntity
			.created(URI.create("/posts/${post.id}"))
			.body(post)
	}

	override fun getPost(id: UUID): PostResponse =
		postService.getPost(id)

	override fun updatePost(authorId: UUID, id: UUID, request: UpdatePostRequest): PostResponse =
		postService.updatePost(authorId, id, request)

	override fun deletePost(authorId: UUID, id: UUID): ResponseEntity<Void> {
		postService.deletePost(authorId, id)

		return ResponseEntity.noContent().build()
	}

	override fun getUserPosts(userId: UUID, pageable: Pageable): Page<PostResponse> =
		postService.getUserPosts(userId, pageable)

	override fun getFeed(pageable: Pageable): Page<PostResponse> =
		postService.getFeed(pageable)

	override fun likePost(userId: UUID, id: UUID): PostLikeResponse =
		postService.likePost(userId, id)

	override fun unlikePost(userId: UUID, id: UUID): ResponseEntity<Void> {
		postService.unlikePost(userId, id)

		return ResponseEntity.noContent().build()
	}

	override fun createComment(
		authorId: UUID,
		id: UUID,
		request: CreateCommentRequest,
	): PostCommentResponse =
		postService.createComment(authorId, id, request)

	override fun getComments(id: UUID, pageable: Pageable): Page<PostCommentResponse> =
		postService.getComments(id, pageable)

	override fun deleteComment(userId: UUID, commentId: UUID): ResponseEntity<Void> {
		postService.deleteComment(userId, commentId)

		return ResponseEntity.noContent().build()
	}
}
