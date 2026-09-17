package com.example.security

import android.Manifest
import com.example.domain.model.PrivacyPermissionGroup

/**
 * Real Permission Risk Analyzer for MrRashed BD.
 * Evaluates Android permissions, dangerous combinations, and privacy exposures.
 */
object PermissionAnalyzer {

    // Sensitive dangerous permissions defined by Android
    private val DANGEROUS_PERMISSIONS_MAP = mapOf(
        Manifest.permission.READ_SMS to ("Read SMS" to "এসএমএস পড়া"),
        Manifest.permission.SEND_SMS to ("Send SMS" to "এসএমএস পাঠানো"),
        Manifest.permission.RECEIVE_SMS to ("Receive SMS" to "এসএমএস গ্রহণ"),
        Manifest.permission.RECORD_AUDIO to ("Record Audio (Microphone)" to "মাইক্রোফোন দিয়ে অডিও রেকর্ড"),
        Manifest.permission.CAMERA to ("Camera Access" to "ক্যামেরা অ্যাক্সেস"),
        Manifest.permission.ACCESS_FINE_LOCATION to ("Precise GPS Location" to "নিখুঁত জিপিএস অবস্থান"),
        Manifest.permission.ACCESS_COARSE_LOCATION to ("Approximate Location" to "আনুমানিক অবস্থান"),
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to ("Background Location" to "ব্যাকগ্রাউন্ড অবস্থান ট্র্যাকিং"),
        Manifest.permission.READ_CONTACTS to ("Read Contacts" to "কন্টাক্টস বা পরিচিতি পড়া"),
        Manifest.permission.WRITE_CONTACTS to ("Modify Contacts" to "কন্টাক্টস পরিবর্তন"),
        Manifest.permission.READ_CALL_LOG to ("Read Call Logs" to "কল হিস্ট্রি পর্যবেক্ষণ"),
        Manifest.permission.CALL_PHONE to ("Make Direct Calls" to "সরাসরি ফোন কল করা"),
        Manifest.permission.READ_PHONE_STATE to ("Read Phone State / IMEI" to "ফোনের অবস্থা ও ডিভাইস আইডি"),
        Manifest.permission.SYSTEM_ALERT_WINDOW to ("Draw Over Other Apps" to "অন্যান্য অ্যাপের উপর প্রদর্শন (Overlay)"),
        Manifest.permission.REQUEST_INSTALL_PACKAGES to ("Install Unknown Apps" to "অজানা অ্যাপ ইনস্টল করার অনুমতি"),
        Manifest.permission.BIND_ACCESSIBILITY_SERVICE to ("Accessibility Control" to "স্ক্রিন পড়া ও নিয়ন্ত্রণ (Accessibility)")
    )

    fun isDangerous(permission: String): Boolean {
        return DANGEROUS_PERMISSIONS_MAP.containsKey(permission) ||
                permission.contains("SMS") ||
                permission.contains("CAMERA") ||
                permission.contains("RECORD_AUDIO") ||
                permission.contains("LOCATION") ||
                permission.contains("CONTACTS") ||
                permission.contains("ALERT_WINDOW") ||
                permission.contains("ACCESSIBILITY")
    }

    fun getFriendlyPermissionName(permission: String, inBengali: Boolean): String {
        val entry = DANGEROUS_PERMISSIONS_MAP[permission]
        return if (entry != null) {
            if (inBengali) entry.second else entry.first
        } else {
            permission.substringAfterLast(".")
        }
    }

    /**
     * Checks if the app permissions match any known dangerous malicious combination.
     */
    fun findDangerousCombinations(permissions: List<String>): List<ThreatIntelligence.PermissionCombo> {
        val permSet = permissions.toSet()
        return ThreatIntelligence.DANGEROUS_COMBINATIONS.filter { combo ->
            combo.requiredPermissions.all { required -> permSet.contains(required) }
        }
    }

    /**
     * Standard privacy groups for the Privacy Guard feature.
     */
    fun getPrivacyAuditGroups(): List<PrivacyPermissionGroup> {
        return listOf(
            PrivacyPermissionGroup(
                groupKey = "CAMERA",
                titleEn = "Camera Access",
                titleBn = "ক্যামেরা অ্যাক্সেস",
                explanationEn = "Apps with this permission can capture photos and videos at any time.",
                explanationBn = "এই অনুমতি পাওয়া অ্যাপগুলো যেকোনো সময় ছবি ও ভিডিও ধারণ করতে পারে।",
                riskSeverity = "HIGH",
                permissionNames = listOf(Manifest.permission.CAMERA)
            ),
            PrivacyPermissionGroup(
                groupKey = "MICROPHONE",
                titleEn = "Microphone Access",
                titleBn = "মাইক্রোফোন ও অডিও রেকর্ড",
                explanationEn = "Apps can record surrounding conversations and ambient audio.",
                explanationBn = "অ্যাপগুলো চারপাশের অডিও ও কথাবার্তা রেকর্ড করার ক্ষমতা রাখে।",
                riskSeverity = "HIGH",
                permissionNames = listOf(Manifest.permission.RECORD_AUDIO)
            ),
            PrivacyPermissionGroup(
                groupKey = "LOCATION",
                titleEn = "Location Tracking",
                titleBn = "অবস্থান ও জিপিএস ট্র্যাকিং",
                explanationEn = "Apps can pinpoint your real-time physical GPS coordinates and movements.",
                explanationBn = "আপনার বাস্তব অবস্থান ও ভ্রমণের ইতিহাস নিখুঁতভাবে ট্র্যাক করতে পারে।",
                riskSeverity = "HIGH",
                permissionNames = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ),
            PrivacyPermissionGroup(
                groupKey = "SMS",
                titleEn = "SMS & OTP Messages",
                titleBn = "এসএমএস ও ওটিপি বার্তা",
                explanationEn = "High risk! Apps can read sensitive two-factor authentication codes and private messages.",
                explanationBn = "উচ্চ ঝুঁকি! অ্যাপগুলো সংবেদনশীল ব্যাংক ওটিপি কোড ও ব্যক্তিগত বার্তা পড়তে পারে।",
                riskSeverity = "HIGH",
                permissionNames = listOf(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS
                )
            ),
            PrivacyPermissionGroup(
                groupKey = "CONTACTS",
                titleEn = "Contacts & Address Book",
                titleBn = "কন্টাক্টস ও ফোনবুক",
                explanationEn = "Apps can access and export your phone contacts, emails, and names.",
                explanationBn = "আপনার ফোনবুকের সংরক্ষিত সব নাম, নম্বর ও ইমেইল পড়তে পারে।",
                riskSeverity = "MEDIUM",
                permissionNames = listOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                )
            ),
            PrivacyPermissionGroup(
                groupKey = "CALL_LOGS",
                titleEn = "Phone & Call Logs",
                titleBn = "কল হিস্ট্রি ও ফোন স্থিতি",
                explanationEn = "Apps can view your complete call history, caller identities, and phone numbers.",
                explanationBn = "কার সাথে কখন কথা বলেছেন সেই সম্পূর্ণ কল তালিকা দেখতে পারে।",
                riskSeverity = "MEDIUM",
                permissionNames = listOf(
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE
                )
            ),
            PrivacyPermissionGroup(
                groupKey = "OVERLAYS",
                titleEn = "Draw Over Apps (Overlay)",
                titleBn = "স্ক্রিন ওভারলে বা নকল উইন্ডো",
                explanationEn = "Can display deceptive floating layers on top of other apps (phishing risk).",
                explanationBn = "অন্যান্য অ্যাপের উপর ভাসমান উইন্ডো দেখিয়ে পাসওয়ার্ড ফাঁদ পাততে পারে।",
                riskSeverity = "HIGH",
                permissionNames = listOf(Manifest.permission.SYSTEM_ALERT_WINDOW)
            ),
            PrivacyPermissionGroup(
                groupKey = "ACCESSIBILITY",
                titleEn = "Accessibility Controls",
                titleBn = "এক্সেসিবিলিটি সুবিধা",
                explanationEn = "Extreme capability: can read all screen text, tap buttons, and bypass protections.",
                explanationBn = "সর্বোচ্চ ক্ষমতা: স্ক্রিনের সব লেখা পড়তে ও স্বয়ংক্রিয়ভাবে বোতাম চাপতে পারে।",
                riskSeverity = "HIGH",
                permissionNames = listOf(Manifest.permission.BIND_ACCESSIBILITY_SERVICE)
            )
        )
    }
}
