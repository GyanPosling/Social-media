package com.socialmedia.userservice.controller

import com.socialmedia.userservice.controller.api.UserApi
import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.request.UpdateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse
import com.socialmedia.userservice.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class UserController(
	private val userService: UserService,
) : UserApi {
	override fun createUser(request: CreateUserRequest): ResponseEntity<UserProfileResponse> {
		val user = userService.createUser(request)

		return ResponseEntity
			.created(URI.create("/users/${user.id}/profile"))
			.body(user)
	}

	override fun getUser(id: Long): UserResponse =
		userService.getUser(id)

	override fun getUserProfile(id: Long): UserProfileResponse =
		userService.getUserProfile(id)

	override fun updateMe(userId: Long, request: UpdateUserRequest): UserProfileResponse =
		userService.updateUser(userId, request)

	override fun searchUsers(query: String): List<UserResponse> =
		userService.searchUsers(query)

	override fun followUser(followerId: Long, id: Long): FollowResponse =
		userService.followUser(followerId = followerId, followingId = id)

	override fun unfollowUser(followerId: Long, id: Long): ResponseEntity<Void> {
		userService.unfollowUser(followerId = followerId, followingId = id)

		return ResponseEntity.noContent().build()
	}

	override fun getFollowers(id: Long): List<UserResponse> =
		userService.getFollowers(id)

	override fun getFollowing(id: Long): List<UserResponse> =
		userService.getFollowing(id)
}
