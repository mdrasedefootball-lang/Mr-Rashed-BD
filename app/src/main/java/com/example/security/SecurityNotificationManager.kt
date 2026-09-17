package com.example.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

/**
 * Real-time Security Notification Dispatcher for MrRashed BD.
 */
class SecurityNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "mrrashed_bd_threats"
        const val CHANNEL_NAME = "Security Threats & Alerts (নিরাপত্তা হুমকি ও সতর্কতা)"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = "MrRashed BD রিয়েল-টাইম অ্যান্টিভাইরাস ও ম্যালওয়্যার সতর্কতা"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showThreatAlert(appName: String, packageName: String, riskReason: String) {
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_PACKAGE_NAME", packageName)
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context,
            packageName.hashCode(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Android official uninstall action intent
        val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:$packageName")
        }
        val uninstallPendingIntent = PendingIntent.getActivity(
            context,
            packageName.hashCode() + 1,
            uninstallIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ বিপজ্জনক হুমকি শনাক্ত হয়েছে: $appName")
            .setContentText(riskReason)
            .setStyle(NotificationCompat.BigTextStyle().bigText("অ্যাপ: $appName ($packageName)\n\nঝুঁকির কারণ: $riskReason\n\nডিভাইসের নিরাপত্তা বজায় রাখতে অ্যাপটি আনইনস্টল করুন।"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)
            .addAction(android.R.drawable.ic_menu_delete, "আনইনস্টল করুন", uninstallPendingIntent)
            .addAction(android.R.drawable.ic_menu_view, "পর্যবেক্ষণ", mainPendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(packageName.hashCode(), builder.build())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
