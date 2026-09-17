package com.example.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.domain.model.ApkSecurityInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * Real APK File Scanner for MrRashed BD.
 * Analyzes standalone APK files before installation to detect threats,
 * suspicious exported components, and permission abuses.
 */
class APKScanner(private val context: Context) {

    suspend fun scanApkFile(apkFile: File): ApkSecurityInfo = withContext(Dispatchers.IO) {
        val pm = context.packageManager

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_PERMISSIONS or
                    PackageManager.GET_SIGNING_CERTIFICATES or
                    PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_SERVICES or
                    PackageManager.GET_RECEIVERS or
                    PackageManager.GET_PROVIDERS
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_PERMISSIONS or
                    PackageManager.GET_SIGNATURES or
                    PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_SERVICES or
                    PackageManager.GET_RECEIVERS or
                    PackageManager.GET_PROVIDERS
        }

        val packageInfo = pm.getPackageArchiveInfo(apkFile.absolutePath, flags)
            ?: throw IllegalArgumentException("APK ফাইলটি রিড করা সম্ভব হয়নি বা ফাইলটি ক্ষতিগ্রস্ত (Invalid or corrupted APK)")

        // Fix sourceDir so resources and labels can be loaded
        packageInfo.applicationInfo?.sourceDir = apkFile.absolutePath
        packageInfo.applicationInfo?.publicSourceDir = apkFile.absolutePath

        val appName = try {
            packageInfo.applicationInfo?.loadLabel(pm)?.toString() ?: apkFile.nameWithoutExtension
        } catch (e: Exception) {
            apkFile.nameWithoutExtension
        }

        val icon = try {
            packageInfo.applicationInfo?.loadIcon(pm)
        } catch (e: Exception) {
            null
        }

        val isDebuggable = if (packageInfo.applicationInfo != null) {
            (packageInfo.applicationInfo!!.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } else false

        val activitiesCount = packageInfo.activities?.size ?: 0
        val servicesCount = packageInfo.services?.size ?: 0
        val receiversCount = packageInfo.receivers?.size ?: 0
        val providersCount = packageInfo.providers?.size ?: 0

        // Count exported components (entry points accessible to third-party apps)
        var exportedCount = 0
        packageInfo.activities?.forEach { if (it.exported) exportedCount++ }
        packageInfo.services?.forEach { if (it.exported) exportedCount++ }
        packageInfo.receivers?.forEach { if (it.exported) exportedCount++ }
        packageInfo.providers?.forEach { if (it.exported) exportedCount++ }

        val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
        val dangerousPermissions = permissions.filter { PermissionAnalyzer.isDangerous(it) }

        // Certificate SHA-256
        val certSha256 = getApkCertFingerprint(packageInfo)

        val minSdk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageInfo.applicationInfo?.minSdkVersion ?: 21
        } else 21

        val targetSdk = packageInfo.applicationInfo?.targetSdkVersion ?: 30

        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }

        // Evaluate risk
        val analysis = AppRiskAnalyzer.evaluate(
            packageName = packageInfo.packageName,
            isSystemApp = false,
            installationSource = "Uninstalled APK File (বাহ্যিক APK প্যাকেজ)",
            permissions = permissions,
            isDebuggable = isDebuggable,
            certificateSha256 = certSha256,
            exportedComponentsCount = exportedCount
        )

        val fileSizeFormatted = formatFileSize(apkFile.length())

        ApkSecurityInfo(
            fileName = apkFile.name,
            filePath = apkFile.absolutePath,
            fileSizeFormatted = fileSizeFormatted,
            appName = appName,
            packageName = packageInfo.packageName,
            versionName = packageInfo.versionName ?: "1.0",
            versionCode = versionCode,
            minSdk = minSdk,
            targetSdk = targetSdk,
            activitiesCount = activitiesCount,
            servicesCount = servicesCount,
            receiversCount = receiversCount,
            providersCount = providersCount,
            exportedComponentsCount = exportedCount,
            permissions = permissions,
            dangerousPermissions = dangerousPermissions,
            certificateSha256 = certSha256,
            isDebuggable = isDebuggable,
            riskLevel = analysis.riskLevel,
            riskScore = analysis.riskScore,
            riskReasons = analysis.reasons,
            icon = icon
        )
    }

    private fun getApkCertFingerprint(pkg: android.content.pm.PackageInfo): String {
        return try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkg.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                pkg.signatures
            }

            if (!signatures.isNullOrEmpty()) {
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signatures[0].toByteArray())
                digest.joinToString(":") { "%02X".format(it) }
            } else {
                "Not available"
            }
        } catch (e: Exception) {
            "Signature extraction error"
        }
    }

    private fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1.0) {
            "%.2f MB".format(mb)
        } else {
            "%.1f KB".format(kb)
        }
    }
}
