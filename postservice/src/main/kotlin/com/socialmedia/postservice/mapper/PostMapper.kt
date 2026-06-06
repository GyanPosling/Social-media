package com.socialmedia.postservice.mapper

import com.socialmedia.postservice.model.entity.Post
import com.socialmedia.postservice.model.entity.PostComment
import com.socialmedia.postservice.model.entity.PostLike
import com.socialmedia.postservice.model.request.CreatePostRequest
import com.socialmedia.postservice.model.response.PostCommentResponse
import com.socialmedia.postservice.model.response.PostLikeResponse
import com.socialmedia.postservice.model.response.PostResponse
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PostMapper {
	fun toEntity(authorId: UUID, request: CreatePostRequest): Post =
		Post(
			authorId = authorId,
			content = request.content,
			imageUrl = request.imageUrl,
		)

	fun toResponse(
		post: Post,
		likesCount: Long,
		commentsCount: Long,
	): PostResponse =
		PostResponse(
			id = post.id,
			authorId = post.authorId,
			content = post.content,
			imageUrl = post.imageUrl,
			likesCount = likesCount,
			commentsCount = commentsCount,
			createdAt = post.createdAt,
			updatedAt = post.updatedAt,
		)

	fun toCommentResponse(comment: PostComment): PostCommentResponse =
		PostCommentResponse(
			id = comment.id,
			postId = comment.postId,
			authorId = comment.authorId,
			content = comment.content,
			createdAt = comment.createdAt,
			updatedAt = comment.updatedAt,
		)

	fun toLikeResponse(postLike: PostLike): PostLikeResponse =
		PostLikeResponse(
			postId = postLike.postId,
			userId = postLike.userId,
			createdAt = postLike.createdAt,
		)
}
