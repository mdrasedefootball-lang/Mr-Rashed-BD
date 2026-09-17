package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an application flagged or quarantined or trusted by the user.
 */
@Entity(tableName = "quarantined_apps")
data class QuarantinedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val riskLevel: String,
    val reasons: String,
    val detectedTime: Long = System.currentTimeMillis(),
    val isWhitelisted: Boolean = false,
    val isUninstalled: Boolean = false
)
