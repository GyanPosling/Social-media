package com.socialmedia.userservice.messaging

import org.springframework.data.jpa.repository.JpaRepository

interface ProcessedKafkaMessageRepository : JpaRepository<ProcessedKafkaMessage, String>
