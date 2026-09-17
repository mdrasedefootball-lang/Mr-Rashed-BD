package com.example.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Standardized risk classification across MrRashed BD security engines.
 */
enum class RiskLevel(
    val titleEn: String,
    val titleBn: String,
    val scoreWeight: Int,
    val hexColor: Long
) {
    SAFE(
        titleEn = "SAFE",
        titleBn = "নিরাপদ",
        scoreWeight = 0,
        hexColor = 0xFF00E599
    ),
    LOW_RISK(
        titleEn = "LOW RISK",
        titleBn = "স্বল্প ঝুঁকি",
        scoreWeight = 15,
        hexColor = 0xFF38BDF8
    ),
    SUSPICIOUS(
        titleEn = "SUSPICIOUS",
        titleBn = "সন্দেহজনক",
        scoreWeight = 45,
        hexColor = 0xFFFBBF24
    ),
    HIGH_RISK(
        titleEn = "HIGH RISK",
        titleBn = "উচ্চ ঝুঁকি",
        scoreWeight = 75,
        hexColor = 0xFFF97316
    ),
    MALWARE_DETECTED(
        titleEn = "MALWARE DETECTED",
        titleBn = "ম্যালওয়্যার শনাক্ত",
        scoreWeight = 100,
        hexColor = 0xFFEF4444
    );

    fun getColor(): Color = Color(hexColor)
}
