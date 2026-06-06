package com.socialmedia.postservice.service

import com.socialmedia.postservice.model.request.CreateCommentRequest
import com.socialmedia.postservice.model.request.CreatePostRequest
import com.socialmedia.postservice.model.request.UpdatePostRequest
import com.socialmedia.postservice.model.response.PostCommentResponse
import com.socialmedia.postservice.model.response.PostLikeResponse
import com.socialmedia.postservice.model.response.PostResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface PostService {
	fun createPost(authorId: UUID, request: CreatePostRequest): PostResponse

	fun getPost(postId: UUID): PostResponse

	fun updatePost(authorId: UUID, postId: UUID, request: UpdatePostRequest): PostResponse

	fun deletePost(authorId: UUID, postId: UUID)

	fun getUserPosts(userId: UUID, pageable: Pageable): Page<PostResponse>

	fun getFeed(pageable: Pageable): Page<PostResponse>

	fun likePost(userId: UUID, postId: UUID): PostLikeResponse

	fun unlikePost(userId: UUID, postId: UUID)

	fun createComment(authorId: UUID, postId: UUID, request: CreateCommentRequest): PostCommentResponse

	fun getComments(postId: UUID, pageable: Pageable): Page<PostCommentResponse>

	fun deleteComment(userId: UUID, commentId: UUID)
}
