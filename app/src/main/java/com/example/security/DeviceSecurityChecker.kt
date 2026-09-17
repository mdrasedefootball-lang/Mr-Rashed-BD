package com.example.security

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.example.domain.model.DeviceSecurityStatus
import java.io.File

/**
 * Real Device Security Checker for MrRashed BD.
 * Checks environment integrity, root indicators, developer mode, and lock screen security.
 */
class DeviceSecurityChecker(private val context: Context) {

    private val SU_PATHS = listOf(
        "/system/bin/su",
        "/system/xbin/su",
        "/sbin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/data/local/su",
        "/system/app/Superuser.apk"
    )

    private val ROOT_PACKAGES = listOf(
        "com.noshufou.android.su",
        "com.thirdparty.superuser",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.topjohnwu.magisk"
    )

    fun checkDeviceSecurity(): DeviceSecurityStatus {
        val rootIndicators = mutableListOf<String>()

        // 1. Check su binary files
        for (path in SU_PATHS) {
            if (File(path).exists()) {
                rootIndicators.add("সিস্টেমে 'su' বাইনারি ফাইল বিদ্যমান ($path) - ডিভাইস রুট করা থাকতে পারে")
            }
        }

        // 2. Check Build.TAGS for test-keys
        val buildTags = Build.TAGS ?: ""
        if (buildTags.contains("test-keys")) {
            rootIndicators.add("কাস্টম রম অথবা টেস্ট-কিজ রম শনাক্ত (Build.TAGS contains test-keys)")
        }

        // 3. Check for root manager packages
        val pm = context.packageManager
        for (pkg in ROOT_PACKAGES) {
            try {
                pm.getPackageInfo(pkg, 0)
                rootIndicators.add("রুট বা সুপার-ইউজার ম্যানেজমেন্ট অ্যাপ উপস্থিত ($pkg)")
            } catch (_: PackageManager.NameNotFoundException) {
                // Not present
            }
        }

        val isRooted = rootIndicators.isNotEmpty()

        // Developer options
        val devOptionsEnabled = try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) != 0
        } catch (e: Exception) {
            false
        }

        // ADB USB Debugging
        val adbEnabled = try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) != 0
        } catch (e: Exception) {
            false
        }

        // Lock screen security
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isScreenLockSecure = keyguardManager?.isDeviceSecure ?: false

        val securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH ?: "Unknown"
        } else {
            "N/A"
        }

        // Tamper inspection of MrRashed BD itself
        val tamperResult = TamperDetector.verifySelfIntegrity(context)

        return DeviceSecurityStatus(
            isRooted = isRooted,
            rootIndicators = rootIndicators,
            areDeveloperOptionsEnabled = devOptionsEnabled,
            isAdbEnabled = adbEnabled,
            isScreenLockSecure = isScreenLockSecure,
            securityPatchDate = securityPatch,
            androidVersion = Build.VERSION.RELEASE ?: "Unknown",
            apiLevel = Build.VERSION.SDK_INT,
            buildTags = buildTags,
            isTampered = tamperResult.isTampered,
            tamperIndicators = tamperResult.reasons,
            keystoreAvailable = EncryptionManager.isKeystoreAvailable()
        )
    }
}
