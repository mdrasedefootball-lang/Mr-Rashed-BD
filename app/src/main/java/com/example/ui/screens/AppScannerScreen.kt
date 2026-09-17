package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.domain.model.AppSecurityInfo
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
import com.example.ui.theme.ThreatWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScannerScreen(
    state: SecurityUiState,
    onBack: () -> Unit,
    onSearchChange: (String) -> Unit,
    onFilterChange: (RiskLevel?) -> Unit,
    onSelectApp: (AppSecurityInfo?) -> Unit,
    onUninstall: (String) -> Unit,
    onOpenSettings: (String) -> Unit,
    onWhitelist: (AppSecurityInfo) -> Unit,
    onQuarantine: (AppSecurityInfo) -> Unit
) {
    val isBn = state.isBengali

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("app_scanner_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (isBn) "অ্যাপ সিকিউরিটি সেন্টার" else "App Security Center",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isBn) "ইনস্টল করা ${state.installedApps.size}টি অ্যাপের ঝুঁকি বিশ্লেষণ"
                    else "Audit of ${state.installedApps.size} installed applications",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // Search Field
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("app_search_field"),
            placeholder = {
                Text(
                    text = if (isBn) "অ্যাপ বা প্যাকেজ নাম দিয়ে খুঁজুন..." else "Search by app or package name...",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberSurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Risk Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = state.filterRiskLevel == null,
                    onClick = { onFilterChange(null) },
                    label = { Text(if (isBn) "সকল (${state.installedApps.size})" else "All (${state.installedApps.size})", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = CyberDarkBg,
                        containerColor = CyberSurface,
                        labelColor = TextSecondary
                    )
                )
            }
            items(RiskLevel.values()) { level ->
                val count = state.installedApps.count { it.riskLevel == level }
                FilterChip(
                    selected = state.filterRiskLevel == level,
                    onClick = { onFilterChange(level) },
                    label = {
                        Text(
                            "${if (isBn) level.titleBn else level.titleEn} ($count)",
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = level.getColor(),
                        selectedLabelColor = CyberDarkBg,
                        containerColor = CyberSurface,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Apps List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.filteredApps, key = { it.packageName }) { app ->
                AppListItemCard(
                    app = app,
                    isBn = isBn,
                    onClick = { onSelectApp(app) }
                )
            }

            if (state.filteredApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBn) "কোনো অ্যাপ পাওয়া যায়নি" else "No applications matched the filter",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // App Detail Bottom Sheet
    if (state.selectedAppForDetail != null) {
        val app = state.selectedAppForDetail
        ModalBottomSheet(
            onDismissRequest = { onSelectApp(null) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CyberSurfaceCard,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (app.icon != null) {
                        Image(
                            bitmap = app.icon.toBitmap(64, 64).asImageBitmap(),
                            contentDescription = app.appName,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = CyberCyan)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.appName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = app.packageName,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "v${app.versionName} • ${app.installationSource}",
                            fontSize = 11.sp,
                            color = CyberCyan
                        )
                    }

                    // Risk Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(app.riskLevel.getColor().copy(alpha = 0.2f))
                            .border(1.dp, app.riskLevel.getColor(), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isBn) app.riskLevel.titleBn else app.riskLevel.titleEn,
                            color = app.riskLevel.getColor(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Explainable Risk Findings
                Text(
                    text = if (isBn) "ঝুঁকি বিশ্লেষণের কারণ ও লক্ষণ:" else "Risk Findings & Explanations:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberDarkBg)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    app.riskReasons.forEach { reason ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = if (app.riskLevel == RiskLevel.SAFE) Icons.Default.Verified else Icons.Default.Warning,
                                contentDescription = null,
                                tint = app.riskLevel.getColor(),
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reason,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Technical Details: Permissions & Certificate
                Text(
                    text = if (isBn) "পারমিশনসমূহ: মোট ${app.requestedPermissions.size}টি (বিপজ্জনক: ${app.dangerousPermissions.size}টি)"
                    else "Permissions: ${app.requestedPermissions.size} total (${app.dangerousPermissions.size} dangerous)",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                if (app.dangerousPermissions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = app.dangerousPermissions.joinToString(", ") { it.substringAfterLast(".") },
                        fontSize = 11.sp,
                        color = ThreatWarning,
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SHA-256: ${app.certificateSha256}",
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons (Official Android Uninstall & System Settings)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!app.isSystemApp) {
                        Button(
                            onClick = {
                                onUninstall(app.packageName)
                                onSelectApp(null)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ThreatDanger)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "আনইনস্টল করুন" else "Uninstall App", fontSize = 13.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            onOpenSettings(app.packageName)
                            onSelectApp(null)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBn) "পারমিশন নিয়ন্ত্রণ" else "Manage App", fontSize = 13.sp, color = CyberCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary actions: Trust / Whitelist or Quarantine
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onWhitelist(app)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBn) "বিশ্বস্ত চিহ্নিত করুন" else "Mark Trusted", fontSize = 12.sp, color = CyberEmerald)
                    }

                    OutlinedButton(
                        onClick = {
                            onQuarantine(app)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ThreatWarning)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = ThreatWarning, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBn) "কোয়ারেন্টাইন লগ" else "Quarantine", fontSize = 12.sp, color = ThreatWarning)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AppListItemCard(
    app: AppSecurityInfo,
    isBn: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("app_card_${app.packageName}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (app.riskLevel == RiskLevel.MALWARE_DETECTED || app.riskLevel == RiskLevel.HIGH_RISK)
                ThreatDanger.copy(alpha = 0.5f)
            else CyberSurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            if (app.icon != null) {
                Image(
                    bitmap = app.icon.toBitmap(48, 48).asImageBitmap(),
                    contentDescription = app.appName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
                Text(
                    text = "v${app.versionName} • ${if (app.isSystemApp) "System" else app.installationSource.take(15)}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Risk Level Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(app.riskLevel.getColor().copy(alpha = 0.15f))
                    .border(1.dp, app.riskLevel.getColor().copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isBn) app.riskLevel.titleBn else app.riskLevel.titleEn,
                    color = app.riskLevel.getColor(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
