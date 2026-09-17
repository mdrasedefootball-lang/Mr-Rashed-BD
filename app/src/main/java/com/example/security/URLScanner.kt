package com.example.security

import com.example.domain.model.RiskLevel
import com.example.domain.model.UrlScanResult
import java.net.URI

/**
 * Real URL & Phishing Scanner for MrRashed BD.
 * Analyzes domains and URLs to identify phishing traps, deceptive hosts, and malicious redirect links.
 */
object URLScanner {

    private val IP_ADDRESS_REGEX = Regex("^(\\d{1,3}\\.){3}\\d{1,3}$")

    fun scanUrl(rawInput: String): UrlScanResult {
        val trimmed = rawInput.trim()
        val formattedUrl = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }

        val indicators = mutableListOf<String>()
        var score = 0

        val uri = try {
            URI(formattedUrl)
        } catch (e: Exception) {
            return UrlScanResult(
                url = trimmed,
                host = "Invalid URI",
                isSafe = false,
                riskLevel = RiskLevel.HIGH_RISK,
                indicators = listOf("ইউআরএল ফরম্যাট অবৈধ বা ত্রুটিপূর্ণ (Malformed URL format)"),
                usesHttps = false,
                isIpHost = false
            )
        }

        val host = uri.host?.lowercase() ?: ""
        val scheme = uri.scheme?.lowercase() ?: "http"
        val usesHttps = scheme == "https"
        val isIpHost = IP_ADDRESS_REGEX.matches(host)
        val pathAndQuery = "${uri.path ?: ""} ${uri.query ?: ""}".lowercase()

        // 1. IP Address as Host
        if (isIpHost) {
            score += 50
            indicators.add("ডোমেইন নামের পরিবর্তে সরাসরি আইপি অ্যাড্রেস ব্যবহার করা হয়েছে (Raw IP Host - High phishing indicator)")
        }

        // 2. HTTPS Security
        if (!usesHttps) {
            score += 20
            indicators.add("অনিরাপদ সংযোগ (Unencrypted plaintext HTTP) - তথ্য চুরি হওয়ার ঝুঁকি রয়েছে")
        }

        // 3. Known Malicious Domain Check
        if (ThreatIntelligence.KNOWN_MALICIOUS_DOMAINS.contains(host)) {
            score += 90
            indicators.add("হুমকি ডাটাবেজে তালিকাভুক্ত বিপজ্জনক ফিশিং ডোমেইন (Known Blacklisted Phishing Domain)")
        }

        // 4. Punycode / Homograph Attack
        if (host.contains("xn--")) {
            score += 45
            indicators.add("হোমোগ্রাফ বা প্রতারণামূলক বর্ণ সংমিশ্রণ শনাক্ত (Punycode/IDN spoofing indicator)")
        }

        // 5. Suspicious TLD check
        val matchedTld = ThreatIntelligence.SUSPICIOUS_TLDS.find { host.endsWith(it) }
        if (matchedTld != null) {
            score += 20
            indicators.add("সাধারণত স্প্যাম ও ফিশিংয়ে ব্যবহৃত TLD ডোমেইন ($matchedTld)")
        }

        // 6. Phishing keywords detection
        for (keyword in ThreatIntelligence.PHISHING_KEYWORDS) {
            if (host.contains(keyword) || pathAndQuery.contains(keyword)) {
                score += 35
                indicators.add("ফিশিং কি-ওয়ার্ড শনাক্ত ('$keyword') - পাসওয়ার্ড বা আর্থিক তথ্য হাতিয়ে নেওয়ার লক্ষণ")
                break
            }
        }

        // 7. Excessive subdomains (e.g. login.paypal.com.verify-user.xyz)
        val subdomainCount = host.count { it == '.' }
        if (subdomainCount >= 4) {
            score += 25
            indicators.add("অস্বাভাবিক সংখ্যক সাব-ডোমেইন ($subdomainCount) - ব্র্যান্ড নকল করার চেষ্টা")
        }

        val finalScore = score.coerceIn(0, 100)
        val isSafe = finalScore < 30

        val riskLevel = when {
            finalScore >= 75 -> RiskLevel.MALWARE_DETECTED
            finalScore >= 50 -> RiskLevel.HIGH_RISK
            finalScore >= 30 -> RiskLevel.SUSPICIOUS
            finalScore >= 15 -> RiskLevel.LOW_RISK
            else -> RiskLevel.SAFE
        }

        if (indicators.isEmpty()) {
            indicators.add("কোনো ক্ষতিকর উপাদান শনাক্ত হয়নি। ডোমেইনটি নিরাপদ মনে হচ্ছে (No threats detected)")
        }

        return UrlScanResult(
            url = formattedUrl,
            host = host.ifEmpty { trimmed },
            isSafe = isSafe,
            riskLevel = riskLevel,
            indicators = indicators,
            usesHttps = usesHttps,
            isIpHost = isIpHost
        )
    }
}
