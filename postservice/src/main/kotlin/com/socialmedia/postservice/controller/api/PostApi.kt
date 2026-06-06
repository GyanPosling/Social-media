package com.socialmedia.postservice.controller.api

import com.socialmedia.postservice.model.request.CreateCommentRequest
import com.socialmedia.postservice.model.request.CreatePostRequest
import com.socialmedia.postservice.model.request.UpdatePostRequest
import com.socialmedia.postservice.model.response.PostCommentResponse
import com.socialmedia.postservice.model.response.PostLikeResponse
import com.socialmedia.postservice.model.response.PostResponse
import com.socialmedia.postservice.security.CurrentUserHeader
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@RequestMapping("/posts")
interface PostApi {
	@PostMapping
	fun createPost(
		@RequestHeader(CurrentUserHeader.NAME) authorId: UUID,
		@Valid @RequestBody request: CreatePostRequest,
	): ResponseEntity<PostResponse>

	@GetMapping("/{id}")
	fun getPost(
		@PathVariable id: UUID,
	): PostResponse

	@PatchMapping("/{id}")
	fun updatePost(
		@RequestHeader(CurrentUserHeader.NAME) authorId: UUID,
		@PathVariable id: UUID,
		@Valid @RequestBody request: UpdatePostRequest,
	): PostResponse

	@DeleteMapping("/{id}")
	fun deletePost(
		@RequestHeader(CurrentUserHeader.NAME) authorId: UUID,
		@PathVariable id: UUID,
	): ResponseEntity<Void>

	@GetMapping("/users/{userId}")
	fun getUserPosts(
		@PathVariable userId: UUID,
		@ParameterObject
		@PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
		pageable: Pageable,
	): Page<PostResponse>

	@GetMapping("/feed")
	fun getFeed(
		@ParameterObject
		@PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
		pageable: Pageable,
	): Page<PostResponse>

	@PostMapping("/{id}/likes")
	fun likePost(
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
		@PathVariable id: UUID,
	): PostLikeResponse

	@DeleteMapping("/{id}/likes")
	fun unlikePost(
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
		@PathVariable id: UUID,
	): ResponseEntity<Void>

	@PostMapping("/{id}/comments")
	fun createComment(
		@RequestHeader(CurrentUserHeader.NAME) authorId: UUID,
		@PathVariable id: UUID,
		@Valid @RequestBody request: CreateCommentRequest,
	): PostCommentResponse

	@GetMapping("/{id}/comments")
	fun getComments(
		@PathVariable id: UUID,
		@ParameterObject
		@PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
		pageable: Pageable,
	): Page<PostCommentResponse>

	@DeleteMapping("/comments/{commentId}")
	fun deleteComment(
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
		@PathVariable commentId: UUID,
	): ResponseEntity<Void>
}
