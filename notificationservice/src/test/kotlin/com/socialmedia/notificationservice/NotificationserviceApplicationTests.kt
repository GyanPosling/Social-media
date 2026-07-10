package com.socialmedia.notificationservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
	properties = [
		"spring.kafka.listener.auto-startup=false",
		"spring.data.mongodb.uri=mongodb://localhost:27017/notificationservice-test",
	],
)
class NotificationserviceApplicationTests {

	@Test
	fun contextLoads() {
	}

}
