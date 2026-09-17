package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.RiskLevel
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
fun WebProtectionScreen(
    state: SecurityUiState,
    onBack: () -> Unit,
    onUrlInputChange: (String) -> Unit,
    onScanUrl: (String?) -> Unit
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
            IconButton(onClick = onBack, modifier = Modifier.testTag("web_protection_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (isBn) "ওয়েব ও ফিশিং প্রটেকশন" else "Web & Phishing Protection",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isBn) "প্রতারণামূলক ওয়েবসাইট, ফিশিং ও ক্ষতিকর লিংক শনাক্তকরণ"
                    else "Analyze URLs for phishing, spoofing & malware downloads",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // URL Input Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBn) "ইউআরএল (URL) লিংক স্ক্যান করুন" else "Analyze URL / Domain",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBn) "কোনো সন্দেহজনক লিংকে প্রবেশের আগে যাচাই করে নিন"
                            else "Verify suspicious links before clicking to prevent credential theft",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = state.urlInput,
                            onValueChange = onUrlInputChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("url_input_field"),
                            placeholder = {
                                Text(
                                    text = "https://example.com/login...",
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Web, contentDescription = null, tint = CyberCyan)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CyberSurfaceCard,
                                unfocusedContainerColor = CyberSurfaceCard,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onScanUrl(null) },
                            enabled = state.urlInput.isNotBlank() && !state.isScanningUrl,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("scan_url_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald)
                        ) {
                            if (state.isScanningUrl) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CyberDarkBg, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null, tint = CyberDarkBg, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "লিংক স্ক্যান করুন" else "Scan Link", color = CyberDarkBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Preset Test Links
            item {
                Column {
                    Text(
                        text = if (isBn) "দ্রুত টেস্ট লিংকসমূহ:" else "Quick Test Presets:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetLinkChip(
                            label = "Safe: Google",
                            isDanger = false,
                            modifier = Modifier.weight(1f),
                            onClick = { onScanUrl("https://www.google.com") }
                        )
                        PresetLinkChip(
                            label = "Phish: Test Bank",
                            isDanger = true,
                            modifier = Modifier.weight(1f),
                            onClick = { onScanUrl("http://phishing-test-bank.xyz/login-verify") }
                        )
                        PresetLinkChip(
                            label = "IP Host Trap",
                            isDanger = true,
                            modifier = Modifier.weight(1f),
                            onClick = { onScanUrl("http://192.168.1.10/appleid-verify") }
                        )
                    }
                }
            }

            // URL Scan Result Display
            if (state.urlScanResult != null) {
                val res = state.urlScanResult
                item {
                    val borderCol = if (res.isSafe) CyberEmerald else ThreatDanger
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("url_scan_result_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderCol.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (res.isSafe) CyberEmerald.copy(alpha = 0.15f) else ThreatDanger.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (res.isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (res.isSafe) ThreatSafe else ThreatDanger,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (res.isSafe) {
                                            if (isBn) "ওয়েবসাইটটি নিরাপদ" else "Website appears Safe"
                                        } else {
                                            if (isBn) "⚠️ সতর্কতা: ক্ষতিকর বা ফিশিং লিংক!" else "⚠️ Warning: Malicious / Phishing Link!"
                                        },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (res.isSafe) ThreatSafe else ThreatDanger
                                    )
                                    Text(
                                        text = res.host,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(res.riskLevel.getColor().copy(alpha = 0.2f))
                                        .border(1.dp, res.riskLevel.getColor(), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isBn) res.riskLevel.titleBn else res.riskLevel.titleEn,
                                        color = res.riskLevel.getColor(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (isBn) "পর্যবেক্ষণের ফলাফল:" else "Security Findings:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberDarkBg)
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                res.indicators.forEach { ind ->
                                    Text(text = "• $ind", fontSize = 11.sp, color = TextPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "HTTPS এনক্রিপশন: ${if (res.usesHttps) "হ্যাঁ (Secure)" else "না (Plaintext HTTP)"}",
                                    fontSize = 11.sp,
                                    color = if (res.usesHttps) CyberEmerald else ThreatWarning
                                )
                                Text(
                                    text = "IP Host: ${if (res.isIpHost) "হ্যাঁ (সন্দেহজনক)" else "না"}",
                                    fontSize = 11.sp,
                                    color = if (res.isIpHost) ThreatDanger else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetLinkChip(
    label: String,
    isDanger: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurface)
            .border(1.dp, if (isDanger) ThreatDanger.copy(alpha = 0.4f) else CyberEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDanger) ThreatDanger else CyberEmerald,
            maxLines = 1
        )
    }
}
