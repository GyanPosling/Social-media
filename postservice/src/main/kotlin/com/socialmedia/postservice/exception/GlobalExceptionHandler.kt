package com.socialmedia.postservice.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
	@ExceptionHandler(PostNotFoundException::class, PostCommentNotFoundException::class)
	fun handleNotFound(
		exception: RuntimeException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		errorResponse(HttpStatus.NOT_FOUND, exception.message ?: "Resource not found", request)

	@ExceptionHandler(InvalidPostOperationException::class)
	fun handleInvalidPostOperation(
		exception: InvalidPostOperationException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		errorResponse(HttpStatus.BAD_REQUEST, exception.message ?: "Invalid post operation", request)

	@ExceptionHandler(DataIntegrityViolationException::class)
	fun handleDataIntegrityViolation(
		exception: DataIntegrityViolationException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		errorResponse(HttpStatus.CONFLICT, "Request violates a database constraint", request)

	@ExceptionHandler(ObjectOptimisticLockingFailureException::class)
	fun handleOptimisticLocking(
		exception: ObjectOptimisticLockingFailureException,
		request: HttpServletRequest,
	): ResponseEntity<ApiErrorResponse> =
		errorResponse(HttpStatus.CONFLICT, "Resource was modified concurrently, retry request", request)

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
