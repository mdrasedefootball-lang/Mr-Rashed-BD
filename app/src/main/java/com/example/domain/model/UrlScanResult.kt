package com.example.domain.model

/**
 * Result of malicious URL, phishing, and deceptive domain analysis.
 */
data class UrlScanResult(
    val url: String,
    val host: String,
    val isSafe: Boolean,
    val riskLevel: RiskLevel,
    val indicators: List<String>,
    val usesHttps: Boolean,
    val isIpHost: Boolean,
    val scannedTimestamp: Long = System.currentTimeMillis()
)
