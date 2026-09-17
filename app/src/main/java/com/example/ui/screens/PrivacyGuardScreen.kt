package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.PrivacyPermissionGroup
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
import com.example.ui.theme.ThreatWarning

@Composable
fun PrivacyGuardScreen(
    state: SecurityUiState,
    onBack: () -> Unit,
    onOpenAppSettings: (String) -> Unit
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
            IconButton(onClick = onBack, modifier = Modifier.testTag("privacy_guard_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (isBn) "প্রাইভেসি গার্ড" else "Privacy Guard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isBn) "সংবেদনশীল অনুমতি ও ডাটা অ্যাক্সেস পর্যবেক্ষণ"
                    else "Audit sensitive permissions & personal data access",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Explanatory Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ThreatWarning.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = ThreatWarning, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isBn) "কোন কোন অ্যাপ আপনার ক্যামেরা, মাইক্রোফোন, জিপিএস লোকেশন বা এসএমএস দেখতে পারে তা নিচে ক্যাটাগরি অনুযায়ী নিরীক্ষা করা হয়েছে।"
                            else "Below are verified apps holding access to your camera, microphone, GPS location, and SMS messages.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Privacy Audit Groups
            items(state.privacyGroups, key = { it.groupKey }) { group ->
                PrivacyGroupCard(
                    group = group,
                    isBn = isBn,
                    onOpenAppSettings = onOpenAppSettings
                )
            }
        }
    }
}

@Composable
private fun PrivacyGroupCard(
    group: PrivacyPermissionGroup,
    isBn: Boolean,
    onOpenAppSettings: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val icon: ImageVector = when (group.groupKey) {
        "CAMERA" -> Icons.Default.CameraAlt
        "MICROPHONE" -> Icons.Default.Mic
        "LOCATION" -> Icons.Default.LocationOn
        "SMS" -> Icons.Default.Sms
        "CONTACTS" -> Icons.Default.Contacts
        "CALL_LOGS" -> Icons.Default.Call
        "OVERLAYS" -> Icons.Default.Window
        "ACCESSIBILITY" -> Icons.Default.Accessibility
        else -> Icons.Default.Security
    }

    val accentColor = when (group.riskSeverity) {
        "HIGH" -> ThreatDanger
        "MEDIUM" -> ThreatWarning
        else -> CyberCyan
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("privacy_group_${group.groupKey}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBn) group.titleBn else group.titleEn,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isBn) "${group.appsHoldingPermission.size}টি অ্যাপের অ্যাক্সেস রয়েছে"
                        else "${group.appsHoldingPermission.size} app(s) have access",
                        fontSize = 11.sp,
                        color = if (group.appsHoldingPermission.isNotEmpty()) ThreatWarning else TextMuted
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }

            // Expanded content: Privacy impact explanation + list of apps
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Explanation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberDarkBg)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (isBn) "ঝুঁকির কারণ: ${group.explanationBn}" else "Why it matters: ${group.explanationEn}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (group.appsHoldingPermission.isEmpty()) {
                        Text(
                            text = if (isBn) "কোনো অ্যাপ এই পারমিশন পায়নি (নিরাপদ)" else "No apps hold this permission (Safe)",
                            fontSize = 12.sp,
                            color = CyberEmerald,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            group.appsHoldingPermission.forEach { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberSurfaceCard)
                                        .clickable { onOpenAppSettings(app.packageName) }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = app.appName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${app.packageName} • ${if (app.isSystemApp) "System" else "User App"}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Open Settings",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
