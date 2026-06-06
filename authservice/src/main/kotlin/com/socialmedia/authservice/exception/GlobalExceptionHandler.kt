package com.socialmedia.authservice.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
	@ExceptionHandler(AuthAccountAlreadyExistsException::class)
	fun handleAccountAlreadyExists(
		exception: AuthAccountAlreadyExistsException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		errorResponse(HttpStatus.CONFLICT, exception.message ?: "Account already exists", request)

	@ExceptionHandler(InvalidCredentialsException::class)
	fun handleInvalidCredentials(
		exception: InvalidCredentialsException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		errorResponse(HttpStatus.UNAUTHORIZED, exception.message ?: "Invalid credentials", request)

	@ExceptionHandler(DataIntegrityViolationException::class)
	fun handleDataIntegrityViolation(
		exception: DataIntegrityViolationException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		errorResponse(HttpStatus.CONFLICT, "Account already exists or violates a database constraint", request)

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidation(
		exception: MethodArgumentNotValidException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> {
		val message = exception.bindingResult.fieldErrors
			.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
			.ifBlank { "Validation failed" }

		return errorResponse(HttpStatus.BAD_REQUEST, message, request)
	}

	@ExceptionHandler(Exception::class)
	fun handleUnexpected(
		exception: Exception,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request)

	private fun errorResponse(
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
