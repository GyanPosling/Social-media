package com.socialmedia.userservice.exception

class UserNotFoundException(userId: Long) : RuntimeException("User with id '$userId' was not found")
