package com.example.domain.model

/**
 * Diagnostic metrics of the host device security configuration and environment integrity.
 */
data class DeviceSecurityStatus(
    val isRooted: Boolean,
    val rootIndicators: List<String>,
    val areDeveloperOptionsEnabled: Boolean,
    val isAdbEnabled: Boolean,
    val isScreenLockSecure: Boolean,
    val securityPatchDate: String,
    val androidVersion: String,
    val apiLevel: Int,
    val buildTags: String,
    val isTampered: Boolean,
    val tamperIndicators: List<String>,
    val keystoreAvailable: Boolean
)
