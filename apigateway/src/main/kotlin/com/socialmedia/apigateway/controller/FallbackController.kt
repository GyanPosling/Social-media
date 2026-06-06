package com.socialmedia.apigateway.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FallbackController {
	@RequestMapping("/fallback")
	fun fallback(): ResponseEntity<Map<String, String>> =
		ResponseEntity
			.status(HttpStatus.SERVICE_UNAVAILABLE)
			.body(
				mapOf(
					"error" to "Service Unavailable",
					"message" to "Downstream service is temporarily unavailable",
				),
			)
}
