package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.ThreatSafe

@Composable
fun SettingsScreen(
    state: SecurityUiState,
    onBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    onToggleRealTime: () -> Unit
) {
    val isBn = state.isBengali

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (isBn) "সেটিংস ও নিরাপত্তা নীতিমালা" else "Settings & Security Manifesto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isBn) "MrRashed BD প্ল্যাটফর্ম কনফিগারেশন" else "MrRashed BD Security Platform",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // General Settings
            item {
                Text(
                    text = if (isBn) "সাধারণ সেটিংস" else "General Settings",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            }

            // Language Toggle Item
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isBn) "অ্যাপের ভাষা (Language)" else "Application Language",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isBn) "বর্তমান ভাষা: বাংলা" else "Current: English",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = !isBn,
                            onCheckedChange = { onToggleLanguage() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberCyan,
                                checkedTrackColor = CyberSurfaceCard,
                                uncheckedThumbColor = CyberEmerald,
                                uncheckedTrackColor = CyberDarkBg
                            )
                        )
                    }
                }
            }

            // Real Time Protection Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyberEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isBn) "রিয়েল-টাইম প্যাকেজ ওয়াচার" else "Real-Time Package Watcher",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isBn) "ইনস্টল হওয়ার সাথে সাথে স্বয়ংক্রিয় স্ক্রিনিং" else "Automatic scan on new installs",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = state.isRealTimeProtectionEnabled,
                            onCheckedChange = { onToggleRealTime() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberEmerald,
                                checkedTrackColor = CyberSurfaceCard,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CyberDarkBg
                            )
                        )
                    }
                }
            }

            // Security Architecture Principles & Android Sandbox Compliance
            item {
                Text(
                    text = if (isBn) "অ্যান্ড্রয়েড নিরাপত্তা আর্কিটেকচার ও প্রতিশ্রুতি" else "Android Security Model & Transparency",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Policy, contentDescription = null, tint = ThreatSafe, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBn) "স্যান্ডবক্স ও ব্যবহারকারীর সম্মতি" else "Sandbox & User Authorization",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBn) "অ্যান্ড্রয়েড ওএস নীতি অনুযায়ী কোনো থার্ড-পার্টি অ্যাপ গোপনে বা অনুমতি ছাড়া অন্য কোনো অ্যাপ মুছে ফেলতে পারে না। MrRashed BD সবসময় অ্যান্ড্রয়েডের অফিশিয়াল সিস্টেম ডায়ালগের মাধ্যমে ব্যবহারকারীর স্পষ্ট অনুমতি নিয়ে ক্ষতিকর অ্যাপ আনইনস্টল করে।"
                            else "Under the Android security sandbox, third-party apps cannot silently remove or modify other applications. MrRashed BD always requires explicit user confirmation via standard Android system uninstallation dialogs.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBn) "লোকাল এনক্রিপশন ও নো-ক্লাউড প্রাইভেসি" else "Hardware Keystore & Zero-Leak Privacy",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBn) "আপনার ডিভাইসের কোনো ফাইল বা ব্যক্তিগত ব্রাউজিং তথ্য বাহ্যিক কোনো সার্ভারে প্রেরণ করা হয় না। সমস্ত নিরাপত্তা অডিট লগ ও ফলাফল ডিভাইসের অভ্যন্তরে Android KeyStore AES-256-GCM ক্রিপ্টোগ্রাফি দিয়ে সুরক্ষিত থাকে।"
                            else "Your personal files, installed apps, and scan logs never leave this device. Local security events are securely stored with Android KeyStore hardware AES-256-GCM cryptography.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "MrRashed BD v1.0.0",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBn) "প্রফেশনাল মোবাইল সিকিউরিটি ও ম্যালওয়্যার ডিফেন্স প্ল্যাটফর্ম।\nকোনো ভুয়া স্ক্যান বা বিভ্রান্তিকর বিজ্ঞাপন নেই।"
                            else "Professional Mobile Security & Anti-Malware Defense Platform.\nClean Architecture + MVVM • No fake antivirus animations.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
