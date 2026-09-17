package com.example.domain.model

import android.graphics.drawable.Drawable

/**
 * Detailed security assessment of a standalone APK file before installation.
 */
data class ApkSecurityInfo(
    val fileName: String,
    val filePath: String,
    val fileSizeFormatted: String,
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val minSdk: Int,
    val targetSdk: Int,
    val activitiesCount: Int,
    val servicesCount: Int,
    val receiversCount: Int,
    val providersCount: Int,
    val exportedComponentsCount: Int,
    val permissions: List<String>,
    val dangerousPermissions: List<String>,
    val certificateSha256: String,
    val isDebuggable: Boolean,
    val riskLevel: RiskLevel,
    val riskScore: Int,
    val riskReasons: List<String>,
    @Transient val icon: Drawable? = null
)
