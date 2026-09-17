package com.example.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Audit group representing sensitive Android hardware/data permissions and apps holding them.
 */
data class PrivacyPermissionGroup(
    val groupKey: String, // CAMERA, RECORD_AUDIO, ACCESS_FINE_LOCATION, etc.
    val titleEn: String,
    val titleBn: String,
    val explanationEn: String,
    val explanationBn: String,
    val riskSeverity: String, // HIGH, MEDIUM, LOW
    val permissionNames: List<String>,
    val appsHoldingPermission: List<AppSecurityInfo> = emptyList()
)
