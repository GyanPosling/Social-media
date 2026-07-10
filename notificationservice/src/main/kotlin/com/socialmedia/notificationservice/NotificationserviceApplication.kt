package com.socialmedia.notificationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
class NotificationserviceApplication

fun main(args: Array<String>) {
	runApplication<NotificationserviceApplication>(*args)
}
