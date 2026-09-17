package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
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
import java.io.File
import java.io.FileOutputStream

@Composable
fun ApkScannerScreen(
    state: SecurityUiState,
    onBack: () -> Unit,
    onScanFile: (File) -> Unit,
    onScanSampleApk: () -> Unit
) {
    val isBn = state.isBengali
    val context = LocalContext.current

    // File picker launcher for .apk documents
    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "scan_target.apk")
                val outputStream = FileOutputStream(tempFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                onScanFile(tempFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("apk_scanner_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (isBn) "ইনস্টল-পূর্ব APK স্ক্যানার" else "Pre-Installation APK Scanner",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isBn) "ফোন বা ফাইল ম্যানেজার থেকে যেকোনো APK যাচাই করুন"
                    else "Audit uninstalled APK files for hidden threats",
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
            // Action Selection Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderZip,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isBn) "APK ফাইল নির্বাচন করুন" else "Select APK File to Audit",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBn) "ইনস্টল করার আগেই APK ফাইলের পারমিশন, এক্সপোর্টেড কম্পোনেন্ট ও ঝুঁকি পরীক্ষা করুন"
                            else "Inspect components, permissions, certificates & malicious signatures before installation",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { apkPickerLauncher.launch("application/vnd.android.package-archive") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pick_apk_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                            ) {
                                Icon(Icons.Default.FileOpen, contentDescription = null, tint = CyberDarkBg, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "ফাইল বাছুন" else "Browse APK", color = CyberDarkBg, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onScanSampleApk,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("scan_sample_apk_button"),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "বর্তমান APK টেস্ট" else "Test Self APK", color = CyberEmerald, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Scanning Progress indicator
            if (state.isScanningApk) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = CyberCyan
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = if (isBn) "APK বিশ্লেষণ করা হচ্ছে..." else "Analyzing APK structure & signatures...",
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Error Card
            if (state.apkScanError != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ThreatDanger)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = ThreatDanger)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.apkScanError,
                                color = ThreatDanger,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Detailed Scan Result
            if (state.apkScanResult != null) {
                val res = state.apkScanResult
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apk_scan_result_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, res.riskLevel.getColor().copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (res.icon != null) {
                                    Image(
                                        bitmap = res.icon.toBitmap(64, 64).asImageBitmap(),
                                        contentDescription = res.appName,
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
                                        text = res.appName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = res.packageName,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "v${res.versionName} • সাইজ: ${res.fileSizeFormatted}",
                                        fontSize = 11.sp,
                                        color = CyberCyan
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(res.riskLevel.getColor().copy(alpha = 0.2f))
                                        .border(1.dp, res.riskLevel.getColor(), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isBn) res.riskLevel.titleBn else res.riskLevel.titleEn,
                                        color = res.riskLevel.getColor(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Reasons
                            Text(
                                text = if (isBn) "নিরাপত্তা ও ঝুঁকির কারণ:" else "Security Indicators:",
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
                                res.riskReasons.forEach { reason ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = if (res.riskLevel == RiskLevel.SAFE) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = res.riskLevel.getColor(),
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

                            Spacer(modifier = Modifier.height(16.dp))

                            // Structural Metrics Grid
                            Text(
                                text = if (isBn) "কম্পোনেন্ট ও আক্রমণ পৃষ্ঠ (Attack Surface):" else "Internal Components:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ComponentStat(title = "Activities", count = res.activitiesCount, modifier = Modifier.weight(1f))
                                ComponentStat(title = "Services", count = res.servicesCount, modifier = Modifier.weight(1f))
                                ComponentStat(title = "Receivers", count = res.receiversCount, modifier = Modifier.weight(1f))
                                ComponentStat(title = "Exported", count = res.exportedComponentsCount, modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Permissions
                            Text(
                                text = if (isBn) "অনুরোধকৃত পারমিশন: ${res.permissions.size}টি (বিপজ্জনক: ${res.dangerousPermissions.size}টি)"
                                else "Permissions: ${res.permissions.size} (${res.dangerousPermissions.size} sensitive)",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            if (res.dangerousPermissions.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = res.dangerousPermissions.joinToString(", ") { it.substringAfterLast(".") },
                                    fontSize = 11.sp,
                                    color = ThreatWarning
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "SDK: Min ${res.minSdk}, Target ${res.targetSdk} • Debuggable: ${res.isDebuggable}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentStat(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberDarkBg)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
            Text(text = title, fontSize = 10.sp, color = TextMuted)
        }
    }
}
