package com.socialmedia.userservice.security

object CurrentUserHeader {
	const val NAME = "X-User-Id"
}

object GatewayHeader {
	const val USER_EMAIL = "X-User-Email"
	const val TIMESTAMP = "X-TS"
	const val SIGNATURE = "X-SIGN"
}
