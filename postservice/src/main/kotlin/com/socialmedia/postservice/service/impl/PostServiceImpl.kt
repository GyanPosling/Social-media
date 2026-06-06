package com.socialmedia.postservice.service.impl

import com.socialmedia.postservice.exception.InvalidPostOperationException
import com.socialmedia.postservice.exception.PostCommentNotFoundException
import com.socialmedia.postservice.exception.PostNotFoundException
import com.socialmedia.postservice.mapper.PostMapper
import com.socialmedia.postservice.model.entity.Post
import com.socialmedia.postservice.model.entity.PostComment
import com.socialmedia.postservice.model.entity.PostLike
import com.socialmedia.postservice.model.request.CreateCommentRequest
import com.socialmedia.postservice.model.request.CreatePostRequest
import com.socialmedia.postservice.model.request.UpdatePostRequest
import com.socialmedia.postservice.model.response.PostCommentResponse
import com.socialmedia.postservice.model.response.PostLikeResponse
import com.socialmedia.postservice.model.response.PostResponse
import com.socialmedia.postservice.repository.PostCommentRepository
import com.socialmedia.postservice.repository.PostLikeRepository
import com.socialmedia.postservice.repository.PostRepository
import com.socialmedia.postservice.service.PostService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional(readOnly = true)
class PostServiceImpl(
	private val postRepository: PostRepository,
	private val postLikeRepository: PostLikeRepository,
	private val postCommentRepository: PostCommentRepository,
	private val postMapper: PostMapper,
) : PostService {
	@Transactional
	override fun createPost(authorId: UUID, request: CreatePostRequest): PostResponse {
		val post = postMapper.toEntity(
			authorId = authorId,
			request = request.copy(content = request.content.trim()),
		)
		val savedPost = postRepository.save(post)

		return savedPost.toResponse()
	}

	override fun getPost(postId: UUID): PostResponse =
		findPost(postId).toResponse()

	@Transactional
	override fun updatePost(authorId: UUID, postId: UUID, request: UpdatePostRequest): PostResponse {
		val post = findPost(postId)
		post.ensureOwner(authorId)

		request.content?.let { post.content = it.trim() }
		request.imageUrl?.let { post.imageUrl = it }
		post.updatedAt = Instant.now()

		return postRepository.save(post).toResponse()
	}

	@Transactional
	override fun deletePost(authorId: UUID, postId: UUID) {
		val post = findPost(postId)
		post.ensureOwner(authorId)

		postRepository.delete(post)
	}

	override fun getUserPosts(userId: UUID, pageable: Pageable): Page<PostResponse> =
		postRepository.findByAuthorId(userId, pageable)
			.map { it.toResponse() }

	override fun getFeed(pageable: Pageable): Page<PostResponse> =
		postRepository.findAll(pageable)
			.map { it.toResponse() }

	@Transactional
	override fun likePost(userId: UUID, postId: UUID): PostLikeResponse {
		ensurePostExists(postId)

		if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
			throw InvalidPostOperationException("User '$userId' already liked post '$postId'")
		}

		val postLike = try {
			postLikeRepository.save(
				PostLike(
					postId = postId,
					userId = userId,
				),
			)
		} catch (exception: DataIntegrityViolationException) {
			throw InvalidPostOperationException("User '$userId' already liked post '$postId'")
		}

		return postMapper.toLikeResponse(postLike)
	}

	@Transactional
	override fun unlikePost(userId: UUID, postId: UUID) {
		ensurePostExists(postId)

		if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
			throw InvalidPostOperationException("User '$userId' has not liked post '$postId'")
		}

		postLikeRepository.deleteByPostIdAndUserId(postId, userId)
	}

	@Transactional
	override fun createComment(
		authorId: UUID,
		postId: UUID,
		request: CreateCommentRequest,
	): PostCommentResponse {
		ensurePostExists(postId)

		val comment = postCommentRepository.save(
			PostComment(
				postId = postId,
				authorId = authorId,
				content = request.content.trim(),
			),
		)

		return postMapper.toCommentResponse(comment)
	}

	override fun getComments(postId: UUID, pageable: Pageable): Page<PostCommentResponse> {
		ensurePostExists(postId)

		return postCommentRepository.findByPostId(postId, pageable)
			.map(postMapper::toCommentResponse)
	}

	@Transactional
	override fun deleteComment(userId: UUID, commentId: UUID) {
		val comment = postCommentRepository.findById(commentId)
			.orElseThrow { PostCommentNotFoundException(commentId) }

		if (comment.authorId != userId) {
			throw InvalidPostOperationException("Only comment author can delete comment '$commentId'")
		}

		postCommentRepository.delete(comment)
	}

	private fun findPost(postId: UUID): Post =
		postRepository.findById(postId)
			.orElseThrow { PostNotFoundException(postId) }

	private fun ensurePostExists(postId: UUID) {
		if (!postRepository.existsById(postId)) {
			throw PostNotFoundException(postId)
		}
	}

	private fun Post.ensureOwner(userId: UUID) {
		if (authorId != userId) {
			throw InvalidPostOperationException("Only post author can modify post '$id'")
		}
	}

	private fun Post.toResponse(): PostResponse =
		postMapper.toResponse(
			post = this,
			likesCount = postLikeRepository.countByPostId(id),
			commentsCount = postCommentRepository.countByPostId(id),
		)
}
