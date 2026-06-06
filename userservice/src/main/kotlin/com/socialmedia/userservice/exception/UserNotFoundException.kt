package com.socialmedia.userservice.exception

import java.util.UUID

class UserNotFoundException(identifier: String) : RuntimeException("User with $identifier was not found") {
	constructor(userId: UUID) : this("id '$userId'")
}
