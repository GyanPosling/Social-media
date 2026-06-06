package com.socialmedia.userservice.exception

class UserAlreadyExistsException(identifier: String) : RuntimeException("User with $identifier already exists")
