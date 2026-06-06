package com.socialmedia.userservice.service.impl

import com.socialmedia.userservice.config.UserCacheNames
import com.socialmedia.userservice.exception.InvalidFollowOperationException
import com.socialmedia.userservice.exception.UserAlreadyExistsException
import com.socialmedia.userservice.exception.UserNotFoundException
import com.socialmedia.userservice.mapper.UserMapper
import com.socialmedia.userservice.model.entity.User
import com.socialmedia.userservice.model.entity.UserFollow
import com.socialmedia.userservice.model.request.CreateUserRequest
import com.socialmedia.userservice.model.request.UpdateUserRequest
import com.socialmedia.userservice.model.response.FollowResponse
import com.socialmedia.userservice.model.response.UserProfileResponse
import com.socialmedia.userservice.model.response.UserResponse
import com.socialmedia.userservice.repository.UserFollowRepository
import com.socialmedia.userservice.repository.UserRepository
import com.socialmedia.userservice.service.UserService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserServiceImpl(
	private val userRepository: UserRepository,
	private val userFollowRepository: UserFollowRepository,
	private val userMapper: UserMapper,
) : UserService {
	@Transactional
	@CachePut(value = [UserCacheNames.USER_PROFILES], key = "#result.id")
	override fun createUser(userId: UUID, userEmail: String?, request: CreateUserRequest): UserProfileResponse {
		val username = request.username.trim().lowercase()
		val trustedEmail = userEmail?.trim()?.lowercase()

		if (userRepository.existsById(userId)) {
			throw UserAlreadyExistsException("id '$userId'")
		}

		if (userRepository.existsByUsername(username)) {
			throw UserAlreadyExistsException("username '$username'")
		}

		val user = userMapper.toEntity(
			userId = userId,
			email = trustedEmail,
			request = request.copy(username = username),
		)
		val savedUser = try {
			userRepository.save(user)
		} catch (exception: DataIntegrityViolationException) {
			throw UserAlreadyExistsException("id '$userId' or username '$username'")
		}

		return userMapper.toProfileResponse(
			user = savedUser,
			followersCount = 0,
			followingCount = 0,
		)
	}

	@Cacheable(value = [UserCacheNames.USERS], key = "#userId", unless = "#result == null")
	override fun getUser(userId: UUID): UserResponse {
		val user = findUserById(userId)

		return userMapper.toResponse(user)
	}

	@Cacheable(value = [UserCacheNames.USER_PROFILES], key = "#userId", unless = "#result == null")
	override fun getUserProfile(userId: UUID): UserProfileResponse {
		val user = findUserById(userId)

		return userMapper.toProfileResponse(
			user = user,
			followersCount = userFollowRepository.countByFollowingId(userId),
			followingCount = userFollowRepository.countByFollowerId(userId),
		)
	}

	@Transactional
	@Caching(
		put = [
			CachePut(value = [UserCacheNames.USER_PROFILES], key = "#result.id"),
		],
		evict = [
			CacheEvict(value = [UserCacheNames.USERS], key = "#result.id"),
		],
	)
	override fun updateUser(userId: UUID, request: UpdateUserRequest): UserProfileResponse {
		val user = findUserById(userId)

		request.displayName?.let { user.displayName = it }
		request.bio?.let { user.bio = it }
		request.avatarUrl?.let { user.avatarUrl = it }
		user.updatedAt = Instant.now()

		val savedUser = userRepository.save(user)
		val savedUserId = savedUser.id

		return userMapper.toProfileResponse(
			user = savedUser,
			followersCount = userFollowRepository.countByFollowingId(savedUserId),
			followingCount = userFollowRepository.countByFollowerId(savedUserId),
		)
	}

	override fun searchUsers(query: String, pageable: Pageable): Page<UserResponse> =
		userRepository.findByUsernameContainingIgnoreCase(query.trim(), pageable)
			.map(userMapper::toResponse)

	@Transactional
	@Caching(
		evict = [
			CacheEvict(value = [UserCacheNames.USER_PROFILES], allEntries = true),
		],
	)
	override fun followUser(followerId: UUID, followingId: UUID): FollowResponse {
		if (followerId == followingId) {
			throw InvalidFollowOperationException("User cannot follow himself")
		}

		ensureUserExists(followerId)
		ensureUserExists(followingId)

		if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
			throw InvalidFollowOperationException("User '$followerId' already follows user '$followingId'")
		}

		val userFollow = userFollowRepository.save(
			UserFollow(
				followerId = followerId,
				followingId = followingId,
			),
		)

		return userMapper.toFollowResponse(userFollow)
	}

	@Transactional
	@Caching(
		evict = [
			CacheEvict(value = [UserCacheNames.USER_PROFILES], allEntries = true),
		],
	)
	override fun unfollowUser(followerId: UUID, followingId: UUID) {
		if (followerId == followingId) {
			throw InvalidFollowOperationException("User cannot unfollow himself")
		}

		ensureUserExists(followerId)
		ensureUserExists(followingId)

		if (!userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
			throw InvalidFollowOperationException("User '$followerId' does not follow user '$followingId'")
		}

		userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId)
	}

	override fun getFollowers(userId: UUID, pageable: Pageable): Page<UserResponse> {
		ensureUserExists(userId)

		return userRepository.findFollowers(userId, pageable)
			.map(userMapper::toResponse)
	}

	override fun getFollowing(userId: UUID, pageable: Pageable): Page<UserResponse> {
		ensureUserExists(userId)

		return userRepository.findFollowing(userId, pageable)
			.map(userMapper::toResponse)
	}

	private fun findUserById(userId: UUID): User =
		userRepository.findById(userId)
			.orElseThrow { UserNotFoundException(userId) }

	private fun ensureUserExists(userId: UUID) {
		if (!userRepository.existsById(userId)) {
			throw UserNotFoundException(userId)
		}
	}
}
