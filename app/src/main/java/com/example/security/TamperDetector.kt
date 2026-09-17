package com.example.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug

/**
 * Self-Tamper and Runtime Integrity Protection for MrRashed BD.
 */
object TamperDetector {

    data class TamperCheckResult(
        val isTampered: Boolean,
        val reasons: List<String>
    )

    fun verifySelfIntegrity(context: Context): TamperCheckResult {
        val reasons = mutableListOf<String>()

        // 1. Check if debugger is actively attached
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) {
            reasons.add("ডিবাগার সংযুক্ত অবস্থায় অ্যাপ্লিকেশনটি রান করছে (Active Debugger Attached)")
        }

        // 2. Check debuggable flag
        val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable) {
            reasons.add("ডিবাগ বিল্ড মোড সক্রিয় (Debug build mode enabled)")
        }

        // 3. Verify expected package name integrity
        val expectedPackagePrefix = "com."
        if (!context.packageName.startsWith(expectedPackagePrefix)) {
            reasons.add("প্যাকেজ নাম পরিবর্তিত হয়েছে (Package name unexpected: ${context.packageName})")
        }

        // 4. Verify signing signature exists
        val pm = context.packageManager
        try {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_SIGNING_CERTIFICATES
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_SIGNATURES
            }
            val packageInfo = pm.getPackageInfo(context.packageName, flags)
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signatures.isNullOrEmpty()) {
                reasons.add("অ্যাপ্লিকেশনের ডিজিটাল স্বাক্ষর অনুপস্থিত (Missing cryptographic APK signature)")
            }
        } catch (e: Exception) {
            reasons.add("স্বাক্ষর যাচাইকালে অপ্রত্যাশিত ত্রুটি: ${e.localizedMessage}")
        }

        return TamperCheckResult(
            isTampered = reasons.any { it.contains("Attached") || it.contains("স্বাক্ষর অনুপস্থিত") },
            reasons = if (reasons.isEmpty()) listOf("অ্যাপ্লিকেশনের অভ্যন্তরীণ নিরাপত্তা ও স্বাক্ষর সুরক্ষিত (Integrity Verified)") else reasons
        )
    }
}
