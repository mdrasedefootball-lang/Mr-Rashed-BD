package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an immutable security audit event logged by MrRashed BD.
 */
@Entity(tableName = "security_events")
data class SecurityEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // SCAN_COMPLETED, THREAT_DETECTED, THREAT_REMOVED, PACKAGE_ADDED, URL_SCANNED, TAMPER_CHECK
    val title: String,
    val details: String,
    val severity: String, // INFO, WARNING, DANGER
    val packageName: String? = null
)
