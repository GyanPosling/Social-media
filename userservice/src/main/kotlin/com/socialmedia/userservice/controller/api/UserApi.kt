package com.socialmedia.userservice.controller.api

import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.request.UpdateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse
import com.socialmedia.userservice.security.CurrentUserHeader
import com.socialmedia.userservice.security.GatewayHeader
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
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
import java.util.UUID

@Tag(name = "Users", description = "User profiles and follows")
@RequestMapping("/users")
interface UserApi {
	@Operation(summary = "Create user profile")
	@PostMapping
	fun createUser(
		@Parameter(description = "Current user id from API Gateway")
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
		@Parameter(description = "Current user email from API Gateway")
		@RequestHeader(GatewayHeader.USER_EMAIL, required = false) userEmail: String?,
		@Valid @RequestBody request: CreateUserRequest,
	): ResponseEntity<UserProfileResponse>

	@Operation(summary = "Get short user information")
	@GetMapping("/{id}")
	fun getUser(
		@Parameter(description = "User id")
		@PathVariable id: UUID,
	): UserResponse

	@Operation(summary = "Get user profile")
	@GetMapping("/{id}/profile")
	fun getUserProfile(
		@Parameter(description = "User id")
		@PathVariable id: UUID,
	): UserProfileResponse

	@Operation(summary = "Update current user profile")
	@PatchMapping("/me")
	fun updateMe(
		@Parameter(description = "Current user id from API Gateway")
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
		@Valid @RequestBody request: UpdateUserRequest,
	): UserProfileResponse

	@Operation(summary = "Search users by username")
	@GetMapping("/search")
	fun searchUsers(
		@RequestParam query: String,
		@ParameterObject
		@PageableDefault(size = 20, sort = ["username"], direction = Sort.Direction.ASC)
		pageable: Pageable,
	): Page<UserResponse>

	@Operation(summary = "Follow user")
	@PostMapping("/{id}/follow")
	fun followUser(
		@Parameter(description = "Current user id from API Gateway")
		@RequestHeader(CurrentUserHeader.NAME) followerId: UUID,
		@Parameter(description = "User id to follow")
		@PathVariable id: UUID,
	): FollowResponse

	@Operation(summary = "Unfollow user")
	@DeleteMapping("/{id}/follow")
	fun unfollowUser(
		@Parameter(description = "Current user id from API Gateway")
		@RequestHeader(CurrentUserHeader.NAME) followerId: UUID,
		@Parameter(description = "User id to unfollow")
		@PathVariable id: UUID,
	): ResponseEntity<Void>

	@Operation(summary = "Get user followers")
	@GetMapping("/{id}/followers")
	fun getFollowers(
		@Parameter(description = "User id")
		@PathVariable id: UUID,
		@ParameterObject
		@PageableDefault(size = 20, sort = ["id"], direction = Sort.Direction.DESC)
		pageable: Pageable,
	): Page<UserResponse>

	@Operation(summary = "Get users followed by user")
	@GetMapping("/{id}/following")
	fun getFollowing(
		@Parameter(description = "User id")
		@PathVariable id: UUID,
		@ParameterObject
		@PageableDefault(size = 20, sort = ["id"], direction = Sort.Direction.DESC)
		pageable: Pageable,
	): Page<UserResponse>
}
