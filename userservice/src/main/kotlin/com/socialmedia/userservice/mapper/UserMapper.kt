package com.socialmedia.userservice.mapper

import com.socialmedia.userservice.model.entity.User
import com.socialmedia.userservice.model.entity.UserFollow
import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserMapper {
	fun toEntity(userId: UUID, email: String?, request: CreateUserRequest): User =
		User(
			id = userId,
			username = request.username.trim().lowercase(),
			email = email?.trim()?.lowercase(),
			phone = request.phone?.trim(),
			displayName = request.displayName,
			bio = request.bio,
			avatarUrl = request.avatarUrl,
		)

	fun toResponse(user: User): UserResponse =
		UserResponse(
			id = user.id,
			username = user.username,
			displayName = user.displayName,
			avatarUrl = user.avatarUrl,
		)

	fun toProfileResponse(
		user: User,
		followersCount: Long,
		followingCount: Long,
	): UserProfileResponse =
		UserProfileResponse(
			id = user.id,
			username = user.username,
			displayName = user.displayName,
			bio = user.bio,
			avatarUrl = user.avatarUrl,
			followersCount = followersCount,
			followingCount = followingCount,
			createdAt = user.createdAt,
		)

	fun toFollowResponse(userFollow: UserFollow): FollowResponse =
		FollowResponse(
			followerId = userFollow.followerId,
			followingId = userFollow.followingId,
			createdAt = userFollow.createdAt,
		)
}
