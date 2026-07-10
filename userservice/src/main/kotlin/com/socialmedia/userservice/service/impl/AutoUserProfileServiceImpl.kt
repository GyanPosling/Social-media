package com.socialmedia.userservice.service.impl

import com.socialmedia.userservice.event.UserRegisteredEvent
import com.socialmedia.userservice.model.entity.User
import com.socialmedia.userservice.repository.UserRepository
import com.socialmedia.userservice.service.AutoUserProfileService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AutoUserProfileServiceImpl(
	private val userRepository: UserRepository,
) : AutoUserProfileService {
	@Transactional
	override fun createProfile(event: UserRegisteredEvent) {
		if (userRepository.existsById(event.userId)) {
			return
		}

		val username = uniqueUsername(event.email, event.userId.toString().replace("-", "").take(8))
		val displayName = username.replace("_", " ")
			.split(" ")
			.joinToString(" ") { word -> word.replaceFirstChar(Char::uppercaseChar) }

		try {
			userRepository.save(
				User(
					id = event.userId,
					username = username,
					email = event.email.trim().lowercase(),
					displayName = displayName,
				),
			)
		} catch (exception: DataIntegrityViolationException) {
			if (!userRepository.existsById(event.userId)) {
				throw exception
			}
		}
	}

	private fun uniqueUsername(email: String, suffix: String): String {
		val base = email.substringBefore("@")
			.lowercase()
			.replace(Regex("[^a-z0-9_]"), "_")
			.trim('_')
			.ifBlank { "user" }
			.let { if (it.length < 3) it.padEnd(3, '_') else it }
			.take(50)

		if (!userRepository.existsByUsername(base)) {
			return base
		}

		val uniqueSuffix = "_$suffix"

		return base.take(50 - uniqueSuffix.length) + uniqueSuffix
	}
}
