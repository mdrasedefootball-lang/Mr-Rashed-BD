package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.NavigationTab
import com.example.ui.SecurityViewModel
import com.example.ui.screens.ApkScannerScreen
import com.example.ui.screens.AppScannerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeviceSecurityScreen
import com.example.ui.screens.PrivacyGuardScreen
import com.example.ui.screens.SecurityLogsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WebProtectionScreen
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SecurityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle notification click intent if app was launched from threat notification
        val alertPackage = intent?.getStringExtra("EXTRA_PACKAGE_NAME")

        setContent {
            MyApplicationTheme {
                // Request notification permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { /* Handled */ }

                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // If launched from threat notification, route directly to App Scanner
                LaunchedEffect(alertPackage) {
                    if (!alertPackage.isNullOrEmpty()) {
                        viewModel.selectTab(NavigationTab.APP_SCANNER)
                        viewModel.updateSearchQuery(alertPackage)
                    }
                }

                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: SecurityViewModel) {
    val state by viewModel.uiState.collectAsState()

    // Handle system back navigation to return to Dashboard if in a sub-screen
    BackHandler(enabled = state.selectedTab != NavigationTab.DASHBOARD) {
        viewModel.selectTab(NavigationTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CyberDarkBg)
        ) {
            when (state.selectedTab) {
                NavigationTab.DASHBOARD -> {
                    DashboardScreen(
                        state = state,
                        onSelectTab = { viewModel.selectTab(it) },
                        onQuickScan = { viewModel.startQuickScan() },
                        onFullScan = { viewModel.startFullScan() },
                        onCancelScan = { viewModel.cancelScan() },
                        onToggleRealTime = { viewModel.toggleRealTimeProtection() },
                        onToggleLanguage = { viewModel.toggleLanguage() }
                    )
                }
                NavigationTab.APP_SCANNER -> {
                    AppScannerScreen(
                        state = state,
                        onBack = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onFilterChange = { viewModel.updateFilterRiskLevel(it) },
                        onSelectApp = { viewModel.selectAppDetail(it) },
                        onUninstall = { viewModel.uninstallApp(it) },
                        onOpenSettings = { viewModel.openAppSettings(it) },
                        onWhitelist = { viewModel.whitelistApp(it) },
                        onQuarantine = { viewModel.quarantineApp(it) }
                    )
                }
                NavigationTab.APK_SCANNER -> {
                    ApkScannerScreen(
                        state = state,
                        onBack = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                        onScanFile = { viewModel.scanApkFile(it) },
                        onScanSampleApk = { viewModel.scanCurrentAppAsApk() }
                    )
                }
                NavigationTab.PRIVACY_GUARD -> {
                    PrivacyGuardScreen(
                        state = state,
                        onBack = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                        onOpenAppSettings = { viewModel.openAppSettings(it) }
                    )
                }
                NavigationTab.WEB_PROTECTION -> {
                    WebProtectionScreen(
                        state = state,
                        onBack = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                        onUrlInputChange = { viewModel.updateUrlInput(it) },
                        onScanUrl = { viewModel.scanUrl(it) }
                    )
                }
                NavigationTab.DEVICE_SECURITY -> {
                    DeviceSecurityScreen(
                        state = state,
                        onBack = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                        onRefresh = { viewModel.refreshDeviceStatus() }
                    )
                }
                NavigationTab.HISTORY -> {
                    SecurityLogsScreen(
                        state = state,
                        onBack = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                        onClearLogs = { viewModel.clearLogs() }
                    )
                }
                NavigationTab.SETTINGS -> {
                    SettingsScreen(
                        state = state,
                        onBack = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onToggleRealTime = { viewModel.toggleRealTimeProtection() }
                    )
                }
            }
        }
    }
}
