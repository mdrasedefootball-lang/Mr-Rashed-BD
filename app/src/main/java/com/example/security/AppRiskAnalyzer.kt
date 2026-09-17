package com.example.security

import com.example.domain.model.RiskLevel

/**
 * Heuristic and Risk Scoring Engine for MrRashed BD.
 * Combines signature matching, permission correlation, installation source,
 * and security flags into an explainable threat profile.
 */
object AppRiskAnalyzer {

    data class AnalysisResult(
        val riskLevel: RiskLevel,
        val riskScore: Int,
        val reasons: List<String>
    )

    fun evaluate(
        packageName: String,
        isSystemApp: Boolean,
        installationSource: String,
        permissions: List<String>,
        isDebuggable: Boolean,
        certificateSha256: String,
        exportedComponentsCount: Int = 0
    ): AnalysisResult {
        val reasons = mutableListOf<String>()
        var score = 0

        // 1. Signature check: Known malicious package patterns
        val lowerPkg = packageName.lowercase()
        val matchedSignature = ThreatIntelligence.SUSPICIOUS_PACKAGE_PATTERNS.find { lowerPkg.contains(it) }
        if (matchedSignature != null) {
            score += 70
            reasons.add("সন্দেহজনক প্যাকেজ নাম প্যাটার্ন শনাক্ত ('$matchedSignature') / Known threat package pattern detected")
        }

        // 2. Dangerous Permission Combinations
        val dangerousCombos = PermissionAnalyzer.findDangerousCombinations(permissions)
        for (combo in dangerousCombos) {
            score += combo.riskScore
            reasons.add("${combo.nameBn} (${combo.nameEn}): ${combo.descriptionBn}")
        }

        // 3. High Risk Individual Permissions
        val dangerousPerms = permissions.filter { PermissionAnalyzer.isDangerous(it) }
        if (dangerousPerms.size >= 6) {
            score += 25
            reasons.add("অতিরিক্ত সংবেদনশীল পারমিশন (${dangerousPerms.size}টি) প্রয়োগ করা হয়েছে / Excessive sensitive permissions granted")
        } else if (dangerousPerms.size in 3..5) {
            score += 10
        }

        // 4. Sideloading / Unknown Installer Source (Heuristic factor only, not alone decisive)
        val isSideloaded = installationSource.contains("Sideload", ignoreCase = true) ||
                installationSource.contains("Unknown", ignoreCase = true)
        if (isSideloaded && !isSystemApp) {
            score += 15
            reasons.add("অফিসিয়াল স্টোরের বাইরে থেকে সাইডলোড করা (Sideloaded from external source)")
        }

        // 5. Debuggable build in production
        if (isDebuggable && !isSystemApp) {
            score += 15
            reasons.add("অ্যাপটি ডিবাগ মোডে বিল্ড করা (Debuggable APK - potential development/tampered build)")
        }

        // 6. Extreme exported component count (Broad attack surface)
        if (exportedComponentsCount > 10 && !isSystemApp) {
            score += 10
            reasons.add("অতিরিক্ত এক্সপোর্টেড কম্পোনেন্ট ($exportedComponentsCount) - সম্ভাব্য অনিরাপদ ইন্টারফেস")
        }

        // System apps are generally trusted system components unless explicitly compromised
        if (isSystemApp) {
            score = (score * 0.25).toInt()
        }

        // Clamp score between 0 and 100
        val finalScore = score.coerceIn(0, 100)

        val riskLevel = when {
            finalScore >= 80 -> RiskLevel.MALWARE_DETECTED
            finalScore >= 60 -> RiskLevel.HIGH_RISK
            finalScore >= 35 -> RiskLevel.SUSPICIOUS
            finalScore >= 15 -> RiskLevel.LOW_RISK
            else -> RiskLevel.SAFE
        }

        if (reasons.isEmpty()) {
            reasons.add("কোনো বিপজ্জনক আচরণ বা হুমকি শনাক্ত হয়নি / No malicious indicators found")
        }

        return AnalysisResult(
            riskLevel = riskLevel,
            riskScore = finalScore,
            reasons = reasons
        )
    }
}
