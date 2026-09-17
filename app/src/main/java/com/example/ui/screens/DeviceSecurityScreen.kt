package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DeviceSecurityStatus
import com.example.ui.SecurityUiState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ThreatDanger
import com.example.ui.theme.ThreatSafe
import com.example.ui.theme.ThreatWarning

@Composable
fun DeviceSecurityScreen(
    state: SecurityUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    val isBn = state.isBengali
    val dev = state.deviceSecurityStatus

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("device_security_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBn) "ডিভাইস সিকিউরিটি চেকার" else "Device Integrity & Security",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isBn) "সিস্টেম রুট, ডেভেলপার অপশন, লক স্ক্রিন ও হার্ডওয়্যার সুরক্ষা"
                    else "Root status, ADB debugging, screen lock & tamper detection",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            IconButton(onClick = onRefresh, modifier = Modifier.testTag("refresh_device_security")) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = CyberCyan)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (dev != null) {
                // 1. Root Status
                item {
                    SecurityDiagnosticCard(
                        title = if (isBn) "সিস্টেম রুট স্ট্যাটাস" else "Root Detection",
                        statusText = if (dev.isRooted) (if (isBn) "রুট শনাক্ত হয়েছে (ঝুঁকিপূর্ণ)" else "Root Detected (Vulnerable)")
                        else (if (isBn) "সিস্টেম রুটহীন (সুরক্ষিত)" else "Not Rooted (Secure)"),
                        isSafe = !dev.isRooted,
                        icon = Icons.Default.Security,
                        details = if (dev.isRooted) dev.rootIndicators.joinToString("\n")
                        else (if (isBn) "কোনো su বাইনারি ফাইল বা সুপার-ইউজার প্যাকেজ শনাক্ত হয়নি।"
                        else "No su binary paths or dangerous root management packages found.")
                    )
                }

                // 2. Screen Lock Security
                item {
                    SecurityDiagnosticCard(
                        title = if (isBn) "লক স্ক্রিন নিরাপত্তা" else "Screen Lock Security",
                        statusText = if (dev.isScreenLockSecure) (if (isBn) "সুরক্ষিত (পিন/প্যাটার্ন/বায়োমেট্রিক)" else "Secured (PIN/Pattern/Biometric)")
                        else (if (isBn) "অরক্ষিত (লক সেট করা নেই)" else "Unsecured (No screen lock)"),
                        isSafe = dev.isScreenLockSecure,
                        icon = Icons.Default.Lock,
                        details = if (dev.isScreenLockSecure) (if (isBn) "ডিভাইস পাসকোড বা ফিঙ্গারপ্রিন্ট দ্বারা সুরক্ষিত রয়েছে।"
                        else "Hardware keyguard credentials configured properly.")
                        else (if (isBn) "সতর্কতা: ফোন হারিয়ে গেলে বা চুরি হলে ডাটা সহজেই অননুমোদিত ব্যক্তির হাতে চলে যেতে পারে।"
                        else "Warning: Set a PIN, password, or fingerprint in Android Settings.")
                    )
                }

                // 3. Developer Options
                item {
                    SecurityDiagnosticCard(
                        title = if (isBn) "ডেভেলপার অপশন" else "Developer Options",
                        statusText = if (dev.areDeveloperOptionsEnabled) (if (isBn) "সক্রিয় (সচেতন থাকুন)" else "Enabled (Caution)")
                        else (if (isBn) "নিষ্ক্রিয় (স্বাভাবিক)" else "Disabled (Standard)"),
                        isSafe = !dev.areDeveloperOptionsEnabled,
                        icon = Icons.Default.DeveloperMode,
                        details = if (dev.areDeveloperOptionsEnabled) (if (isBn) "ডেভেলপার মোড চালু থাকলে ডিভাইসে কাস্টম ডিবাগিং সম্ভব।"
                        else "Developer settings are turned on. Third-party testing may bypass protections.")
                        else (if (isBn) "ডেভেলপার সেটিংস বন্ধ রয়েছে।" else "Standard consumer configuration.")
                    )
                }

                // 4. USB Debugging (ADB)
                item {
                    SecurityDiagnosticCard(
                        title = if (isBn) "ইউএসবি ডিবাগিং (ADB)" else "USB Debugging (ADB)",
                        statusText = if (dev.isAdbEnabled) (if (isBn) "সক্রিয় (ঝুঁকি রয়েছে)" else "Enabled (Potential Risk)")
                        else (if (isBn) "বন্ধ (সুরক্ষিত)" else "Disabled (Safe)"),
                        isSafe = !dev.isAdbEnabled,
                        icon = Icons.Default.Usb,
                        details = if (dev.isAdbEnabled) (if (isBn) "কম্পিউটারের মাধ্যমে ফোনের ডাটা এক্সট্র্যাক্ট বা অননুমোদিত অ্যাপ ইনস্টল করা সম্ভব হতে পারে।"
                        else "Warning: ADB allows command-line data extraction when connected to unknown USB ports.")
                        else (if (isBn) "ইউএসবি পোর্টের মাধ্যমে ডিবাগিং সংযোগ বন্ধ রয়েছে।" else "USB debugging bridge is inactive.")
                    )
                }

                // 5. App Tamper & Integrity Check (MrRashed BD Self-Defense)
                item {
                    SecurityDiagnosticCard(
                        title = if (isBn) "অ্যাপ সেলফ-ইন্টেগ্রিটি (Tamper Protection)" else "Self-Defense & Tamper Check",
                        statusText = if (!dev.isTampered) (if (isBn) "অটুট ও নিরাপদ" else "Verified & Intact")
                        else (if (isBn) "সতর্কতা: পরিবর্তন শনাক্ত" else "Warning: Tampering Detected"),
                        isSafe = !dev.isTampered,
                        icon = Icons.Default.VpnKey,
                        details = dev.tamperIndicators.joinToString("\n")
                    )
                }

                // 6. Hardware Keystore Encryption
                item {
                    SecurityDiagnosticCard(
                        title = if (isBn) "হার্ডওয়্যার কি-স্টোর এনক্রিপশন" else "Hardware Keystore Encryption",
                        statusText = if (dev.keystoreAvailable) (if (isBn) "সক্রিয় (AES-256-GCM)" else "Active (AES-256-GCM)")
                        else (if (isBn) "অনুপলব্ধ" else "Unavailable"),
                        isSafe = dev.keystoreAvailable,
                        icon = Icons.Default.Key,
                        details = if (dev.keystoreAvailable) (if (isBn) "অ্যান্ড্রয়েড সিকিউর এনক্লেভ ও হার্ডওয়্যার কি-স্টোর সক্রিয় রয়েছে।"
                        else "Android KeyStore provider active with AES-256 Galois/Counter Mode.")
                        else "Software fallback cryptographic provider active."
                    )
                }

                // 7. System OS & Patch Info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isBn) "ডিভাইস ও ওএস স্পেসিফিকেশন" else "Device & OS Specifications",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Android সংস্করণ: ${dev.androidVersion} (API ${dev.apiLevel})", fontSize = 12.sp, color = TextPrimary)
                            Text(text = "সিকিউরিটি প্যাচ: ${dev.securityPatchDate}", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Build Tags: ${dev.buildTags}", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityDiagnosticCard(
    title: String,
    statusText: String,
    isSafe: Boolean,
    icon: ImageVector,
    details: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSafe) CyberSurfaceBorder else ThreatWarning.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSafe) CyberEmerald.copy(alpha = 0.15f) else ThreatWarning.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSafe) ThreatSafe else ThreatWarning,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSafe) ThreatSafe else ThreatWarning
                    )
                }

                Icon(
                    imageVector = if (isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isSafe) ThreatSafe else ThreatWarning,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberDarkBg)
                    .padding(10.dp)
            ) {
                Text(
                    text = details,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
