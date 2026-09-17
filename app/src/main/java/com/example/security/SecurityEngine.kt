package com.example.security

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.entity.SecurityEventEntity
import com.example.domain.model.AppSecurityInfo
import com.example.domain.model.DeviceSecurityStatus
import com.example.domain.model.PrivacyPermissionGroup
import com.example.domain.model.RiskLevel
import com.example.domain.model.SecurityReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Main Central Security Engine coordinating scanners, privacy audits,
 * device integrity verifications, and threat mitigations.
 */
class SecurityEngine(private val context: Context) {

    val malwareScanner = MalwareScanner(context)
    val apkScanner = APKScanner(context)
    val deviceSecurityChecker = DeviceSecurityChecker(context)
    val threatResponseManager = ThreatResponseManager(context)
    val database = AppDatabase.getDatabase(context)

    /**
     * Computes the holistic security health report based on scan results and device status.
     */
    suspend fun generateSecurityReport(
        scannedApps: List<AppSecurityInfo>,
        deviceStatus: DeviceSecurityStatus,
        isRealTimeProtectionActive: Boolean,
        lastScanTime: Long
    ): SecurityReport = withContext(Dispatchers.Default) {
        var penalty = 0

        val threatsFound = scannedApps.count {
            it.riskLevel == RiskLevel.MALWARE_DETECTED || it.riskLevel == RiskLevel.HIGH_RISK
        }
        val riskyApps = scannedApps.count { it.riskLevel == RiskLevel.HIGH_RISK }
        val suspiciousApps = scannedApps.count { it.riskLevel == RiskLevel.SUSPICIOUS }

        // Penalties for malware / high risk
        penalty += threatsFound * 35
        penalty += suspiciousApps * 8

        // Device vulnerabilities penalty
        var deviceVulnerabilities = 0
        if (deviceStatus.isRooted) {
            penalty += 35
            deviceVulnerabilities++
        }
        if (deviceStatus.isTampered) {
            penalty += 30
            deviceVulnerabilities++
        }
        if (deviceStatus.isAdbEnabled) {
            penalty += 10
            deviceVulnerabilities++
        }
        if (!deviceStatus.isScreenLockSecure) {
            penalty += 15
            deviceVulnerabilities++
        }

        // Privacy issues (e.g. non-system apps with SMS or Accessibility)
        val privacyIssues = scannedApps.count { app ->
            !app.isSystemApp && app.dangerousPermissions.any {
                it.contains("SMS") || it.contains("ACCESSIBILITY") || it.contains("OVERLAY")
            }
        }
        if (privacyIssues > 2) {
            penalty += (privacyIssues - 2) * 4
        }

        if (!isRealTimeProtectionActive) {
            penalty += 15
        }

        val calculatedScore = (100 - penalty).coerceIn(0, 100)
        val isDeviceSecure = calculatedScore >= 80 && threatsFound == 0 && !deviceStatus.isRooted

        val titleBn = when {
            threatsFound > 0 -> "উচ্চ ঝুঁকির হুমকি বা ম্যালওয়্যার শনাক্ত হয়েছে!"
            calculatedScore < 60 -> "ডিভাইসের নিরাপত্তা ঝুঁকিপূর্ণ"
            calculatedScore < 80 -> "মনোযোগ প্রয়োজন: সম্ভাব্য ঝুঁকি শনাক্ত"
            else -> "আপনার ফোন সম্পূর্ণ সুরক্ষিত"
        }

        val titleEn = when {
            threatsFound > 0 -> "High Risk Threats Detected!"
            calculatedScore < 60 -> "Device Security Compromised"
            calculatedScore < 80 -> "Attention Required: Moderate Risks"
            else -> "Your Device is Fully Protected"
        }

        SecurityReport(
            securityScore = calculatedScore,
            totalAppsScanned = scannedApps.size,
            threatsFoundCount = threatsFound,
            riskyAppsCount = riskyApps,
            suspiciousAppsCount = suspiciousApps,
            privacyIssuesCount = privacyIssues,
            deviceVulnerabilitiesCount = deviceVulnerabilities,
            isRealTimeProtectionActive = isRealTimeProtectionActive,
            isDeviceSecure = isDeviceSecure,
            lastScanTimestamp = lastScanTime,
            overallStatusTitleBn = titleBn,
            overallStatusTitleEn = titleEn
        )
    }

    /**
     * Audits apps into privacy permission groups.
     */
    fun auditPrivacyGroups(apps: List<AppSecurityInfo>): List<PrivacyPermissionGroup> {
        val baseGroups = PermissionAnalyzer.getPrivacyAuditGroups()
        return baseGroups.map { group ->
            val matchingApps = apps.filter { app ->
                app.requestedPermissions.any { group.permissionNames.contains(it) }
            }
            group.copy(appsHoldingPermission = matchingApps)
        }
    }

    /**
     * Records a scan completion security event into the audit log.
     */
    suspend fun logScanCompleted(scannedCount: Int, threatsCount: Int, scanType: String) = withContext(Dispatchers.IO) {
        val severity = if (threatsCount > 0) "DANGER" else "INFO"
        val title = if (threatsCount > 0) {
            "⚠️ $scanType সম্পন্ন: ${threatsCount}টি হুমকি শনাক্ত!"
        } else {
            "✅ $scanType সফলভাবে সম্পন্ন (কোনো হুমকি নেই)"
        }
        val details = "মোট ${scannedCount}টি অ্যাপ্লিকেশন স্ক্যান করা হয়েছে। সনাক্তকৃত হুমকি: $threatsCount"

        database.securityEventDao().insertEvent(
            SecurityEventEntity(
                eventType = "SCAN_COMPLETED",
                title = title,
                details = details,
                severity = severity
            )
        )
    }
}
