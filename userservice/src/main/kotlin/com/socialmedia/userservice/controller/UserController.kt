package com.socialmedia.userservice.controller

import com.socialmedia.userservice.controller.api.UserApi
import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.request.UpdateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse
import com.socialmedia.userservice.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class UserController(
	private val userService: UserService,
) : UserApi {
	override fun createUser(
		userId: UUID,
		userEmail: String?,
		request: CreateUserRequest,
	): ResponseEntity<UserProfileResponse> {
		val user = userService.createUser(userId, userEmail, request)

		return ResponseEntity
			.created(URI.create("/users/${user.id}/profile"))
			.body(user)
	}

	override fun getUser(id: UUID): UserResponse =
		userService.getUser(id)

	override fun getUserProfile(id: UUID): UserProfileResponse =
		userService.getUserProfile(id)

	override fun updateMe(userId: UUID, request: UpdateUserRequest): UserProfileResponse =
		userService.updateUser(userId, request)

	override fun searchUsers(query: String, pageable: Pageable): Page<UserResponse> =
		userService.searchUsers(query, pageable)

	override fun followUser(followerId: UUID, id: UUID): FollowResponse =
		userService.followUser(followerId = followerId, followingId = id)

	override fun unfollowUser(followerId: UUID, id: UUID): ResponseEntity<Void> {
		userService.unfollowUser(followerId = followerId, followingId = id)

		return ResponseEntity.noContent().build()
	}

	override fun getFollowers(id: UUID, pageable: Pageable): Page<UserResponse> =
		userService.getFollowers(id, pageable)

	override fun getFollowing(id: UUID, pageable: Pageable): Page<UserResponse> =
		userService.getFollowing(id, pageable)
}
