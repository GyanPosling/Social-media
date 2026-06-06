package com.socialmedia.authservice.exception

class AuthAccountAlreadyExistsException(email: String) : RuntimeException("Account with email '$email' already exists")
