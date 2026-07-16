package com.socialmedia.notificationservice.messaging

import org.springframework.data.mongodb.repository.MongoRepository

interface ProcessedKafkaMessageRepository : MongoRepository<ProcessedKafkaMessage, String>
