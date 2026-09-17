package com.example.security

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.data.AppDatabase
import com.example.data.entity.QuarantinedAppEntity
import com.example.data.entity.SecurityEventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Handles threat remediation, user-approved removal via Android official intents,
 * and quarantine/whitelist states.
 */
class ThreatResponseManager(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)

    /**
     * Triggers Android's official package uninstallation dialog with explicit user authorization.
     */
    fun startUninstallFlow(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Opens Android Application Details Settings for permission revocation.
     */
    fun openAppSystemSettings(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    suspend fun quarantineApp(packageName: String, appName: String, riskLevel: String, reasons: String) = withContext(Dispatchers.IO) {
        db.quarantinedAppDao().insertOrUpdate(
            QuarantinedAppEntity(
                packageName = packageName,
                appName = appName,
                riskLevel = riskLevel,
                reasons = reasons,
                isWhitelisted = false,
                isUninstalled = false
            )
        )
        db.securityEventDao().insertEvent(
            SecurityEventEntity(
                eventType = "THREAT_QUARANTINED",
                title = "অ্যাপ কোয়ারেন্টাইনে নেওয়া হয়েছে: $appName",
                details = "প্যাকেজ: $packageName. কারণ: $reasons",
                severity = "WARNING",
                packageName = packageName
            )
        )
    }

    suspend fun whitelistApp(packageName: String, appName: String) = withContext(Dispatchers.IO) {
        db.quarantinedAppDao().insertOrUpdate(
            QuarantinedAppEntity(
                packageName = packageName,
                appName = appName,
                riskLevel = "SAFE",
                reasons = "User marked as trusted",
                isWhitelisted = true,
                isUninstalled = false
            )
        )
        db.securityEventDao().insertEvent(
            SecurityEventEntity(
                eventType = "APP_WHITELISTED",
                title = "অ্যাপ বিশ্বস্ত তালিকায় যোগ করা হয়েছে: $appName",
                details = "ব্যবহারকারী কর্তৃক যাচাইকৃত ও অনুমোদিত",
                severity = "INFO",
                packageName = packageName
            )
        )
    }

    suspend fun removeQuarantine(packageName: String) = withContext(Dispatchers.IO) {
        db.quarantinedAppDao().deleteByPackage(packageName)
    }
}
