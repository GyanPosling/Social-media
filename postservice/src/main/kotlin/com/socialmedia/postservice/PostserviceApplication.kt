package com.socialmedia.postservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PostserviceApplication

fun main(args: Array<String>) {
	runApplication<PostserviceApplication>(*args)
}
