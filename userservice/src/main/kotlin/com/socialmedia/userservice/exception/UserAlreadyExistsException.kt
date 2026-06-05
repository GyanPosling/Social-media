package com.socialmedia.userservice.exception

class UserAlreadyExistsException(username: String) : RuntimeException("User with username '$username' already exists")
