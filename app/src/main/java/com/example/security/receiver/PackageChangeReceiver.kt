package com.example.security.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.AppDatabase
import com.example.data.entity.SecurityEventEntity
import com.example.domain.model.RiskLevel
import com.example.security.MalwareScanner
import com.example.security.SecurityNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Real-time BroadcastReceiver monitoring newly installed, updated, or removed applications.
 */
class PackageChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val packageName = intent.data?.schemeSpecificPart ?: return

        // Skip our own app package to prevent self-trigger loops
        if (packageName == context.packageName) return

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val db = AppDatabase.getDatabase(context)

        when (action) {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED -> {
                coroutineScope.launch {
                    try {
                        val pm = context.packageManager
                        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            PackageManager.GET_PERMISSIONS or PackageManager.GET_SIGNING_CERTIFICATES
                        } else {
                            @Suppress("DEPRECATION")
                            PackageManager.GET_PERMISSIONS or PackageManager.GET_SIGNATURES
                        }

                        val packageInfo = pm.getPackageInfo(packageName, flags)
                        val scanner = MalwareScanner(context)
                        val analysis = scanner.analyzePackage(packageInfo, pm)

                        val isThreat = analysis.riskLevel == RiskLevel.HIGH_RISK ||
                                analysis.riskLevel == RiskLevel.MALWARE_DETECTED

                        db.securityEventDao().insertEvent(
                            SecurityEventEntity(
                                eventType = if (isThreat) "THREAT_DETECTED" else "PACKAGE_ADDED",
                                title = if (isThreat) "⚠️ ক্ষতিকর অ্যাপ ইনস্টল করা হয়েছে: ${analysis.appName}" else "নতুন অ্যাপ ইনস্টল হয়েছে: ${analysis.appName}",
                                details = "প্যাকেজ: $packageName. ঝুঁকির মাত্রা: ${analysis.riskLevel.titleBn}. কারণ: ${analysis.riskReasons.joinToString(", ")}",
                                severity = if (isThreat) "DANGER" else "INFO",
                                packageName = packageName
                            )
                        )

                        if (isThreat) {
                            val notificationManager = SecurityNotificationManager(context)
                            notificationManager.showThreatAlert(
                                appName = analysis.appName,
                                packageName = packageName,
                                riskReason = analysis.riskReasons.firstOrNull() ?: "উচ্চ ঝুঁকি উপাদান শনাক্ত"
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                coroutineScope.launch {
                    db.securityEventDao().insertEvent(
                        SecurityEventEntity(
                            eventType = "PACKAGE_REMOVED",
                            title = "অ্যাপ আনইনস্টল বা অপসারিত হয়েছে",
                            details = "প্যাকেজ: $packageName সফলভাবে সিস্টেম থেকে মুছে ফেলা হয়েছে",
                            severity = "INFO",
                            packageName = packageName
                        )
                    )
                    db.quarantinedAppDao().deleteByPackage(packageName)
                }
            }
        }
    }
}
