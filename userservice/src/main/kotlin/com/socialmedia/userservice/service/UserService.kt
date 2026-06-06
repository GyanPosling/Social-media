package com.socialmedia.userservice.service

import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.request.UpdateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface UserService {
	fun createUser(userId: UUID, userEmail: String?, request: CreateUserRequest): UserProfileResponse

	fun getUser(userId: UUID): UserResponse

	fun getUserProfile(userId: UUID): UserProfileResponse

	fun updateUser(userId: UUID, request: UpdateUserRequest): UserProfileResponse

	fun searchUsers(query: String, pageable: Pageable): Page<UserResponse>

	fun followUser(followerId: UUID, followingId: UUID): FollowResponse

	fun unfollowUser(followerId: UUID, followingId: UUID)

	fun getFollowers(userId: UUID, pageable: Pageable): Page<UserResponse>

	fun getFollowing(userId: UUID, pageable: Pageable): Page<UserResponse>
}
