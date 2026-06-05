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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional(readOnly = true)
class UserServiceImpl(
	private val userRepository: UserRepository,
	private val userFollowRepository: UserFollowRepository,
	private val userMapper: UserMapper,
) : UserService {
	@Transactional
	@CachePut(value = [UserCacheNames.USER_PROFILES], key = "#result.id")
	override fun createUser(request: CreateUserRequest): UserProfileResponse {
		if (userRepository.existsByUsername(request.username)) {
			throw UserAlreadyExistsException(request.username)
		}

		val user = userMapper.toEntity(request)
		val savedUser = userRepository.save(user)

		return userMapper.toProfileResponse(
			user = savedUser,
			followersCount = 0,
			followingCount = 0,
		)
	}

	@Cacheable(value = [UserCacheNames.USERS], key = "#userId", unless = "#result == null")
	override fun getUser(userId: Long): UserResponse {
		val user = findUserById(userId)

		return userMapper.toResponse(user)
	}

	@Cacheable(value = [UserCacheNames.USER_PROFILES], key = "#userId", unless = "#result == null")
	override fun getUserProfile(userId: Long): UserProfileResponse {
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
			CachePut(value = [UserCacheNames.USER_PROFILES], key = "#userId"),
		],
		evict = [
			CacheEvict(value = [UserCacheNames.USERS], key = "#userId"),
		],
	)
	override fun updateUser(userId: Long, request: UpdateUserRequest): UserProfileResponse {
		val user = findUserById(userId)

		request.displayName?.let { user.displayName = it }
		request.bio?.let { user.bio = it }
		request.avatarUrl?.let { user.avatarUrl = it }
		user.updatedAt = Instant.now()

		val savedUser = userRepository.save(user)

		return userMapper.toProfileResponse(
			user = savedUser,
			followersCount = userFollowRepository.countByFollowingId(userId),
			followingCount = userFollowRepository.countByFollowerId(userId),
		)
	}

	override fun searchUsers(query: String): List<UserResponse> {
		return userRepository.findByUsernameContainingIgnoreCase(query)
			.map(userMapper::toResponse)
	}

	@Transactional
	@Caching(
		evict = [
			CacheEvict(value = [UserCacheNames.USER_PROFILES], key = "#followerId"),
			CacheEvict(value = [UserCacheNames.USER_PROFILES], key = "#followingId"),
		],
	)
	override fun followUser(followerId: Long, followingId: Long): FollowResponse {
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
			CacheEvict(value = [UserCacheNames.USER_PROFILES], key = "#followerId"),
			CacheEvict(value = [UserCacheNames.USER_PROFILES], key = "#followingId"),
		],
	)
	override fun unfollowUser(followerId: Long, followingId: Long) {
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

	override fun getFollowers(userId: Long): List<UserResponse> {
		ensureUserExists(userId)

		val followerIds = userFollowRepository.findByFollowingId(userId)
			.map(UserFollow::followerId)

		return userRepository.findAllById(followerIds)
			.map(userMapper::toResponse)
	}

	override fun getFollowing(userId: Long): List<UserResponse> {
		ensureUserExists(userId)

		val followingIds = userFollowRepository.findByFollowerId(userId)
			.map(UserFollow::followingId)

		return userRepository.findAllById(followingIds)
			.map(userMapper::toResponse)
	}

	private fun findUserById(userId: Long): User =
		userRepository.findById(userId)
			.orElseThrow { UserNotFoundException(userId) }

	private fun ensureUserExists(userId: Long) {
		if (!userRepository.existsById(userId)) {
			throw UserNotFoundException(userId)
		}
	}
}
