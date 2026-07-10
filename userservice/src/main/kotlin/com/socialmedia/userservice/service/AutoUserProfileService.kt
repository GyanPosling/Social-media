package com.socialmedia.userservice.service

import com.socialmedia.userservice.event.UserRegisteredEvent

interface AutoUserProfileService {
	fun createProfile(event: UserRegisteredEvent)
}
