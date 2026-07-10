package com.socialmedia.notificationservice.controller.api

import com.socialmedia.notificationservice.model.response.NotificationResponse
import com.socialmedia.notificationservice.security.CurrentUserHeader
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@RequestMapping("/notifications")
interface NotificationApi {
	@GetMapping
	fun getNotifications(
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
		@ParameterObject
		@PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
		pageable: Pageable,
	): Page<NotificationResponse>

	@GetMapping("/unread-count")
	fun getUnreadCount(
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
	): Map<String, Long>

	@PatchMapping("/{id}/read")
	fun markAsRead(
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
		@PathVariable id: String,
	): NotificationResponse

	@PatchMapping("/read-all")
	fun markAllAsRead(
		@RequestHeader(CurrentUserHeader.NAME) userId: UUID,
	): ResponseEntity<Void>
}
