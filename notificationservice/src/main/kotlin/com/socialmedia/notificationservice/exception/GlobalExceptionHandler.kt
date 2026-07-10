package com.socialmedia.notificationservice.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
	@ExceptionHandler(NotificationNotFoundException::class)
	fun handleNotFound(
		exception: NotificationNotFoundException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		error(HttpStatus.NOT_FOUND, exception.message.orEmpty(), request)

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidation(
		exception: MethodArgumentNotValidException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> {
		val message = exception.bindingResult.fieldErrors
			.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
			.ifBlank { "Validation failed" }

		return error(HttpStatus.BAD_REQUEST, message, request)
	}

	@ExceptionHandler(Exception::class)
	fun handleUnexpected(
		exception: Exception,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		error(HttpStatus.INTERNAL_SERVER_ERROR, exception.message ?: "Unexpected error", request)

	private fun error(
		status: HttpStatus,
		message: String,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		ResponseEntity.status(status).body(
			ApiErrorResponse(
				status = status.value(),
				error = status.reasonPhrase,
				message = message,
				path = request.requestURI,
			),
		)
}
