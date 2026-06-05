package com.socialmedia.userservice.controller.api

import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.request.UpdateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse
import com.socialmedia.userservice.security.CurrentUserHeader
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Users", description = "User profiles and follows")
@RequestMapping("/users")
interface UserApi {
	@Operation(summary = "Create user profile")
	@PostMapping
	fun createUser(
		@Valid @RequestBody request: CreateUserRequest,
	): ResponseEntity<UserProfileResponse>

	@Operation(summary = "Get short user information")
	@GetMapping("/{id}")
	fun getUser(
		@Parameter(description = "User id")
		@PathVariable id: Long,
	): UserResponse

	@Operation(summary = "Get user profile")
	@GetMapping("/{id}/profile")
	fun getUserProfile(
		@Parameter(description = "User id")
		@PathVariable id: Long,
	): UserProfileResponse

	@Operation(summary = "Update current user profile")
	@PatchMapping("/me")
	fun updateMe(
		@Parameter(description = "Current user id from API Gateway")
		@RequestHeader(CurrentUserHeader.NAME) userId: Long,
		@Valid @RequestBody request: UpdateUserRequest,
	): UserProfileResponse

	@Operation(summary = "Search users by username")
	@GetMapping("/search")
	fun searchUsers(
		@RequestParam query: String,
	): List<UserResponse>

	@Operation(summary = "Follow user")
	@PostMapping("/{id}/follow")
	fun followUser(
		@Parameter(description = "Current user id from API Gateway")
		@RequestHeader(CurrentUserHeader.NAME) followerId: Long,
		@Parameter(description = "User id to follow")
		@PathVariable id: Long,
	): FollowResponse

	@Operation(summary = "Unfollow user")
	@DeleteMapping("/{id}/follow")
	fun unfollowUser(
		@Parameter(description = "Current user id from API Gateway")
		@RequestHeader(CurrentUserHeader.NAME) followerId: Long,
		@Parameter(description = "User id to unfollow")
		@PathVariable id: Long,
	): ResponseEntity<Void>

	@Operation(summary = "Get user followers")
	@GetMapping("/{id}/followers")
	fun getFollowers(
		@Parameter(description = "User id")
		@PathVariable id: Long,
	): List<UserResponse>

	@Operation(summary = "Get users followed by user")
	@GetMapping("/{id}/following")
	fun getFollowing(
		@Parameter(description = "User id")
		@PathVariable id: Long,
	): List<UserResponse>
}
