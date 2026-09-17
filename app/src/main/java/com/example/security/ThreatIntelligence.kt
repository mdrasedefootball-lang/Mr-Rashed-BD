package com.example.security

import android.Manifest

/**
 * Built-in Threat Intelligence module for MrRashed BD.
 * Contains heuristic rules, known threat signatures, adware indicators,
 * dangerous permission correlations, and phishing domain heuristics.
 */
object ThreatIntelligence {

    // Known malicious / spyware / adware package naming patterns
    val SUSPICIOUS_PACKAGE_PATTERNS = listOf(
        "spyware",
        "keylogger",
        "stealthtracker",
        "stalkerware",
        "rat.client",
        "trojan.dropper",
        "fakeupdate.android",
        "banking.phish",
        "adware.clicker",
        "sms.fraud",
        "miner.monero",
        "ransom.locker",
        "hidden.payload"
    )

    // Known dangerous or abuse-prone permissions
    val HIGH_RISK_PERMISSIONS = listOf(
        Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
        Manifest.permission.SYSTEM_ALERT_WINDOW,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.REQUEST_INSTALL_PACKAGES,
        Manifest.permission.PACKAGE_USAGE_STATS
    )

    // Dangerous permission combinations indicating specific malware behaviors
    data class PermissionCombo(
        val nameEn: String,
        val nameBn: String,
        val requiredPermissions: List<String>,
        val riskScore: Int,
        val descriptionEn: String,
        val descriptionBn: String
    )

    val DANGEROUS_COMBINATIONS = listOf(
        PermissionCombo(
            nameEn = "Banking Trojan Indicator",
            nameBn = "ব্যাংকিং ট্রোজান লক্ষণ",
            requiredPermissions = listOf(
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_SMS,
                Manifest.permission.INTERNET
            ),
            riskScore = 80,
            descriptionEn = "App can display deceptive overlay windows over banking apps while intercepting SMS OTP codes.",
            descriptionBn = "অ্যাপটি ওটিপি (SMS OTP) দেখতে পারে এবং স্ক্রিনে নকল উইন্ডো দেখিয়ে পাসওয়ার্ড চুরি করতে পারে।"
        ),
        PermissionCombo(
            nameEn = "Covert Spyware Indicator",
            nameBn = "গুপ্ত স্পাইওয়্যার লক্ষণ",
            requiredPermissions = listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
            ),
            riskScore = 75,
            descriptionEn = "Simultaneous access to Camera, Microphone, Exact Location, and Network allows continuous surveillance.",
            descriptionBn = "একই সাথে ক্যামেরা, মাইক্রোফোন, নিখুঁত লোকেশন এবং ইন্টারনেট ব্যবহার করে ব্যবহারকারীর উপর নজরদারি সম্ভব।"
        ),
        PermissionCombo(
            nameEn = "Malware Dropper / Sideloading Indicator",
            nameBn = "ম্যালওয়্যার ড্রপার লক্ষণ",
            requiredPermissions = listOf(
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                Manifest.permission.INTERNET,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            ),
            riskScore = 65,
            descriptionEn = "App can silently download and prompt installation of secondary APK payloads while obscuring UI.",
            descriptionBn = "অ্যাপটি ইন্টারনেট থেকে অন্যান্য বিপজ্জনক অ্যাপ বা ফাইল নামিয়ে গোপনে ইনস্টল করতে উদ্বুদ্ধ করতে পারে।"
        ),
        PermissionCombo(
            nameEn = "SMS Toll Fraud / Smishing Indicator",
            nameBn = "এসএমএস প্রতারণা লক্ষণ",
            requiredPermissions = listOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE
            ),
            riskScore = 60,
            descriptionEn = "App can send premium-rate SMS messages and read incoming confirmation texts without user awareness.",
            descriptionBn = "অ্যাপটি ব্যবহারকারীর অজান্তে প্রিমিয়াম এসএমএস পাঠাতে বা প্রাপ্ত এসএমএস গোপন করতে পারে।"
        ),
        PermissionCombo(
            nameEn = "Accessibility Abuse Indicator",
            nameBn = "এক্সেসিবিলিটি অপব্যবহার লক্ষণ",
            requiredPermissions = listOf(
                Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
                Manifest.permission.INTERNET
            ),
            riskScore = 70,
            descriptionEn = "Accessibility service can read all screen text, capture credentials, and simulate touches on other apps.",
            descriptionBn = "এক্সেসিবিলিটি সুবিধা ব্যবহার করে স্ক্রিনের লেখা পড়তে, পাসওয়ার্ড ক্যাপচার করতে এবং স্পর্শ নিয়ন্ত্রণ করতে পারে।"
        )
    )

    // Phishing keywords in domain or URL paths
    val PHISHING_KEYWORDS = listOf(
        "login-verify",
        "secure-account",
        "update-wallet",
        "free-gift",
        "verify-identity",
        "recover-password",
        "claim-reward",
        "banking-security",
        "appleid-verify",
        "google-security-alert",
        "paypal-resolution",
        "crypto-airdrop",
        "account-suspended"
    )

    // Suspicious top-level domains frequently abused for zero-cost phishing
    val SUSPICIOUS_TLDS = listOf(
        ".top", ".work", ".click", ".gq", ".cf", ".ml", ".tk", ".ga", ".buzz", ".xyz", ".cc"
    )

    // Known malicious domains test database
    val KNOWN_MALICIOUS_DOMAINS = setOf(
        "phishing-test-bank.xyz",
        "secure-login-apple-verify.cf",
        "free-crypto-giveaway-bonus.top",
        "malware-payload-drop.buzz",
        "steal-credentials.work",
        "account-update-portal-alert.click"
    )
}
