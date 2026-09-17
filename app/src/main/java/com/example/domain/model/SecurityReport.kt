package com.example.domain.model

/**
 * Aggregated security health report for MrRashed BD dashboard.
 */
data class SecurityReport(
    val securityScore: Int, // 0 to 100
    val totalAppsScanned: Int,
    val threatsFoundCount: Int,
    val riskyAppsCount: Int,
    val suspiciousAppsCount: Int,
    val privacyIssuesCount: Int,
    val deviceVulnerabilitiesCount: Int,
    val isRealTimeProtectionActive: Boolean,
    val isDeviceSecure: Boolean,
    val lastScanTimestamp: Long,
    val overallStatusTitleBn: String,
    val overallStatusTitleEn: String
)
