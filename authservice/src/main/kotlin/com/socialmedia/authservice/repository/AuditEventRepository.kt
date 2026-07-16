package com.socialmedia.authservice.repository

import com.socialmedia.authservice.model.entity.AuditEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AuditEventRepository : JpaRepository<AuditEvent, UUID>
