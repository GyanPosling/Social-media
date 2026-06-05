package com.socialmedia.userservice.mapper

import com.socialmedia.userservice.model.entity.User
import com.socialmedia.userservice.model.entity.UserFollow
import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper {
	fun toEntity(request: CreateUserRequest): User =
		User(
			username = request.username,
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
