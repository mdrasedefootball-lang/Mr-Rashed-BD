package com.example.domain.model

import android.graphics.drawable.Drawable

/**
 * Detailed security assessment of an installed Android application.
 */
data class AppSecurityInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val installationSource: String, // Google Play Store, Package Installer, Sideloaded
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val requestedPermissions: List<String>,
    val dangerousPermissions: List<String>,
    val certificateSha256: String,
    val isDebuggable: Boolean,
    val riskLevel: RiskLevel,
    val riskScore: Int, // 0 to 100
    val riskReasons: List<String>,
    val isQuarantined: Boolean = false,
    val isWhitelisted: Boolean = false,
    @Transient val icon: Drawable? = null
)
