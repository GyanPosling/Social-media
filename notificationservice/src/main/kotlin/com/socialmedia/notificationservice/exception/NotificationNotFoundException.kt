package com.socialmedia.notificationservice.exception

class NotificationNotFoundException(id: String) : RuntimeException("Notification '$id' was not found")
