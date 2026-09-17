package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NavigationTab
import com.example.ui.SecurityUiState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberElectricBlue
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
fun DashboardScreen(
    state: SecurityUiState,
    onSelectTab: (NavigationTab) -> Unit,
    onQuickScan: () -> Unit,
    onFullScan: () -> Unit,
    onCancelScan: () -> Unit,
    onToggleRealTime: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val isBn = state.isBengali
    val report = state.securityReport
    val isProtected = report.isDeviceSecure && report.threatsFoundCount == 0

    // Pulsing animation for active protection shield
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (state.isScanning) 1.08f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MrRashed BD",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "PROTECT • DETECT • DEFEND",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberCyan,
                        letterSpacing = 1.5.sp
                    )
                }

                // Language switcher pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CyberSurfaceCard)
                        .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
                        .clickable { onToggleLanguage() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("toggle_language_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "বাংলা (BN)" else "English (EN)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }
        }

        // Hero Security Status Card
        item {
            val statusGradient = if (isProtected) {
                Brush.verticalGradient(listOf(Color(0xFF064E3B).copy(alpha = 0.6f), CyberSurfaceCard))
            } else {
                Brush.verticalGradient(listOf(Color(0xFF881337).copy(alpha = 0.6f), CyberSurfaceCard))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_status_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isProtected) CyberEmerald.copy(alpha = 0.4f) else ThreatDanger.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(statusGradient)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Shield Icon with glow
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                if (isProtected) CyberEmerald.copy(alpha = 0.15f)
                                else ThreatDanger.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isProtected) Icons.Default.Shield else Icons.Default.Warning,
                            contentDescription = "Security Shield",
                            tint = if (isProtected) ThreatSafe else ThreatDanger,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isBn) report.overallStatusTitleBn else report.overallStatusTitleEn,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isProtected) {
                            if (isBn) "রিয়েল-টাইম অ্যান্টিভাইরাস সুরক্ষা ও ম্যালওয়্যার গার্ড সক্রিয় রয়েছে"
                            else "Real-time antivirus protection & malware guard active"
                        } else {
                            if (isBn) "${report.threatsFoundCount}টি ক্ষতিকর উপাদান অবিলম্বে পর্যালোচনা করুন"
                            else "${report.threatsFoundCount} high risk threat(s) need immediate attention"
                        },
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Score Dial Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberDarkBg.copy(alpha = 0.7f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isBn) "নিরাপত্তা স্কোর: " else "Security Score: ",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${report.securityScore}/100",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                report.securityScore >= 80 -> ThreatSafe
                                report.securityScore >= 60 -> ThreatWarning
                                else -> ThreatDanger
                            }
                        )
                    }
                }
            }
        }

        // Scanning In-Progress Card
        item {
            AnimatedVisibility(visible = state.isScanning) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = CyberCyan
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isBn) "স্ক্যানিং চলছে..." else "Scanning in progress...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            IconButton(
                                onClick = onCancelScan,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Cancel",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { state.scanProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyberEmerald,
                            trackColor = CyberSurfaceBorder
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = state.currentScanningApp,
                                fontSize = 12.sp,
                                color = CyberCyan,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${state.scannedAppsCount} / ${state.totalAppsToScan}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Real-Time Protection Switch Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (state.isRealTimeProtectionEnabled) CyberEmerald.copy(alpha = 0.2f)
                                    else TextMuted.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (state.isRealTimeProtectionEnabled) ThreatSafe else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBn) "রিয়েল-টাইম সুরক্ষা" else "Real-Time Protection",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isBn) "নতুন অ্যাপ ইনস্টলেশন ও পরিবর্তন পর্যবেক্ষণ"
                                else "Monitors newly installed APKs & package updates",
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
                        ),
                        modifier = Modifier.testTag("realtime_protection_switch")
                    )
                }
            }
        }

        // Scan Action Buttons (Quick Scan & Full Scan)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onQuickScan,
                    enabled = !state.isScanning,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_scan_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = CyberDarkBg,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "কুইক স্ক্যান" else "Quick Scan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberDarkBg
                    )
                }

                OutlinedButton(
                    onClick = onFullScan,
                    enabled = !state.isScanning,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("full_scan_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "ফুল স্ক্যান" else "Full Scan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }
        }

        // Quick Security Health Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = if (isBn) "হুমকি" else "Threats",
                    value = "${report.threatsFoundCount}",
                    color = if (report.threatsFoundCount > 0) ThreatDanger else ThreatSafe,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = if (isBn) "ঝুঁকিপূর্ণ" else "Risky Apps",
                    value = "${report.riskyAppsCount + report.suspiciousAppsCount}",
                    color = if (report.riskyAppsCount > 0) ThreatWarning else ThreatSafe,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = if (isBn) "প্রাইভেসি" else "Privacy Issues",
                    value = "${report.privacyIssuesCount}",
                    color = if (report.privacyIssuesCount > 0) ThreatWarning else ThreatSafe,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = if (isBn) "ডিভাইস" else "Device Status",
                    value = if (state.deviceSecurityStatus?.isRooted == true) "রুট" else "সুরক্ষিত",
                    color = if (state.deviceSecurityStatus?.isRooted == true) ThreatDanger else ThreatSafe,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Title: Security Modules
        item {
            Text(
                text = if (isBn) "সিকিউরিটি মডিউলসমূহ" else "Security Modules",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Navigation Grid of Modules
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleNavCard(
                        title = if (isBn) "অ্যাপ স্ক্যানার" else "App Scanner",
                        subtitle = if (isBn) "${state.installedApps.size}টি অ্যাপ ইনস্টলড" else "${state.installedApps.size} apps scanned",
                        icon = Icons.Default.Android,
                        accentColor = CyberEmerald,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_app_scanner"),
                        onClick = { onSelectTab(NavigationTab.APP_SCANNER) }
                    )
                    ModuleNavCard(
                        title = if (isBn) "APK স্ক্যানার" else "APK Scanner",
                        subtitle = if (isBn) "ইনস্টল-পূর্ব প্রিভিউ" else "Pre-install audit",
                        icon = Icons.Default.FolderZip,
                        accentColor = CyberCyan,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_apk_scanner"),
                        onClick = { onSelectTab(NavigationTab.APK_SCANNER) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleNavCard(
                        title = if (isBn) "প্রাইভেসি গার্ড" else "Privacy Guard",
                        subtitle = if (isBn) "ক্যামেরা, লোকেশন, এসএমএস" else "Camera, Mic, SMS audits",
                        icon = Icons.Default.Visibility,
                        accentColor = ThreatWarning,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_privacy_guard"),
                        onClick = { onSelectTab(NavigationTab.PRIVACY_GUARD) }
                    )
                    ModuleNavCard(
                        title = if (isBn) "ওয়েব ও ফিশিং" else "Web Protection",
                        subtitle = if (isBn) "ক্ষতিকর লিংক স্ক্যানার" else "Phishing & URL Scanner",
                        icon = Icons.Default.Web,
                        accentColor = CyberElectricBlue,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_web_protection"),
                        onClick = { onSelectTab(NavigationTab.WEB_PROTECTION) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleNavCard(
                        title = if (isBn) "ডিভাইস সিকিউরিটি" else "Device Security",
                        subtitle = if (isBn) "রুট, এডিবি, প্যাচ স্থিতি" else "Root, ADB, Integrity",
                        icon = Icons.Default.Lock,
                        accentColor = CyberEmerald,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_device_security"),
                        onClick = { onSelectTab(NavigationTab.DEVICE_SECURITY) }
                    )
                    ModuleNavCard(
                        title = if (isBn) "সিকিউরিটি হিস্ট্রি" else "Security Logs",
                        subtitle = if (isBn) "${state.securityEvents.size}টি রেকর্ড" else "${state.securityEvents.size} events logged",
                        icon = Icons.Default.History,
                        accentColor = CyberCyan,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_security_history"),
                        onClick = { onSelectTab(NavigationTab.HISTORY) }
                    )
                }

                ModuleNavCard(
                    title = if (isBn) "সেটিংস ও টেম্পার প্রটেকশন" else "Settings & Integrity",
                    subtitle = if (isBn) "ভাষা পরিবর্তন, কি-স্টোর এনক্রিপশন ও অ্যান্ড্রয়েড পলিসি" else "Language, Keystore encryption & Sandbox notes",
                    icon = Icons.Default.Settings,
                    accentColor = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("nav_settings"),
                    onClick = { onSelectTab(NavigationTab.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ModuleNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
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
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
