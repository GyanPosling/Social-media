package com.socialmedia.userservice.service

import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.request.UpdateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse

interface UserService {
	fun createUser(request: CreateUserRequest): UserProfileResponse

	fun getUser(userId: Long): UserResponse

	fun getUserProfile(userId: Long): UserProfileResponse

	fun updateUser(userId: Long, request: UpdateUserRequest): UserProfileResponse

	fun searchUsers(query: String): List<UserResponse>

	fun followUser(followerId: Long, followingId: Long): FollowResponse

	fun unfollowUser(followerId: Long, followingId: Long)

	fun getFollowers(userId: Long): List<UserResponse>

	fun getFollowing(userId: Long): List<UserResponse>
}
