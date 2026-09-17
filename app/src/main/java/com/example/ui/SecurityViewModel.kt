package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.SecurityEventEntity
import com.example.domain.model.ApkSecurityInfo
import com.example.domain.model.AppSecurityInfo
import com.example.domain.model.DeviceSecurityStatus
import com.example.domain.model.PrivacyPermissionGroup
import com.example.domain.model.RiskLevel
import com.example.domain.model.SecurityReport
import com.example.domain.model.UrlScanResult
import com.example.security.MalwareScanner
import com.example.security.SecurityEngine
import com.example.security.URLScanner
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class NavigationTab {
    DASHBOARD,
    APP_SCANNER,
    APK_SCANNER,
    PRIVACY_GUARD,
    WEB_PROTECTION,
    DEVICE_SECURITY,
    HISTORY,
    SETTINGS
}

data class SecurityUiState(
    val selectedTab: NavigationTab = NavigationTab.DASHBOARD,
    val isBengali: Boolean = true, // Bangla primary by default
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val currentScanningApp: String = "",
    val totalAppsToScan: Int = 0,
    val scannedAppsCount: Int = 0,
    val installedApps: List<AppSecurityInfo> = emptyList(),
    val filteredApps: List<AppSecurityInfo> = emptyList(),
    val searchQuery: String = "",
    val filterRiskLevel: RiskLevel? = null,
    val selectedAppForDetail: AppSecurityInfo? = null,
    val securityReport: SecurityReport = SecurityReport(
        securityScore = 100,
        totalAppsScanned = 0,
        threatsFoundCount = 0,
        riskyAppsCount = 0,
        suspiciousAppsCount = 0,
        privacyIssuesCount = 0,
        deviceVulnerabilitiesCount = 0,
        isRealTimeProtectionActive = true,
        isDeviceSecure = true,
        lastScanTimestamp = 0,
        overallStatusTitleBn = "আপনার ফোন সুরক্ষিত",
        overallStatusTitleEn = "Your Device is Protected"
    ),
    val deviceSecurityStatus: DeviceSecurityStatus? = null,
    val privacyGroups: List<PrivacyPermissionGroup> = emptyList(),
    val isRealTimeProtectionEnabled: Boolean = true,
    val urlInput: String = "",
    val urlScanResult: UrlScanResult? = null,
    val isScanningUrl: Boolean = false,
    val apkScanResult: ApkSecurityInfo? = null,
    val isScanningApk: Boolean = false,
    val apkScanError: String? = null,
    val securityEvents: List<SecurityEventEntity> = emptyList()
)

class SecurityViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = SecurityEngine(application)
    private var scanJob: Job? = null

    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        refreshDeviceStatus()
        observeSecurityEvents()
        // Auto-run an initial scan on startup to populate real device state
        startQuickScan()
    }

    fun selectTab(tab: NavigationTab) {
        _uiState.update { it.copy(selectedTab = tab, selectedAppForDetail = null) }
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(isBengali = !it.isBengali) }
    }

    fun toggleRealTimeProtection() {
        val newState = !_uiState.value.isRealTimeProtectionEnabled
        _uiState.update { it.copy(isRealTimeProtectionEnabled = newState) }
        viewModelScope.launch {
            val status = engine.deviceSecurityChecker.checkDeviceSecurity()
            val report = engine.generateSecurityReport(
                scannedApps = _uiState.value.installedApps,
                deviceStatus = status,
                isRealTimeProtectionActive = newState,
                lastScanTime = _uiState.value.securityReport.lastScanTimestamp
            )
            _uiState.update { it.copy(securityReport = report) }
            engine.database.securityEventDao().insertEvent(
                SecurityEventEntity(
                    eventType = "PROTECTION_TOGGLED",
                    title = if (newState) "রিয়েল-টাইম সুরক্ষা সক্রিয় করা হয়েছে" else "⚠️ রিয়েল-টাইম সুরক্ষা বন্ধ করা হয়েছে",
                    details = if (newState) "ব্যাকগ্রাউন্ড পর্যবেক্ষণ চলছে" else "ডিভাইস সুরক্ষা হ্রাস পেতে পারে",
                    severity = if (newState) "INFO" else "WARNING"
                )
            )
        }
    }

    fun refreshDeviceStatus() {
        viewModelScope.launch {
            val status = engine.deviceSecurityChecker.checkDeviceSecurity()
            _uiState.update { it.copy(deviceSecurityStatus = status) }
        }
    }

    private fun observeSecurityEvents() {
        viewModelScope.launch {
            engine.database.securityEventDao().getAllEvents().collect { events ->
                _uiState.update { it.copy(securityEvents = events) }
            }
        }
    }

    fun startQuickScan() {
        startScan(isQuickScan = true)
    }

    fun startFullScan() {
        startScan(isQuickScan = false)
    }

    private fun startScan(isQuickScan: Boolean) {
        scanJob?.cancel()
        _uiState.update {
            it.copy(
                isScanning = true,
                scanProgress = 0f,
                currentScanningApp = "প্রস্তুত হচ্ছে...",
                scannedAppsCount = 0
            )
        }

        scanJob = viewModelScope.launch {
            val scanFlow = engine.malwareScanner.scanInstalledApps(isQuickScan = isQuickScan)
            scanFlow.collect { event ->
                when (event) {
                    is MalwareScanner.ScanEvent.Progress -> {
                        val progress = if (event.totalCount > 0) {
                            event.currentCount.toFloat() / event.totalCount
                        } else 0f
                        _uiState.update {
                            it.copy(
                                scanProgress = progress,
                                currentScanningApp = event.currentApp,
                                scannedAppsCount = event.currentCount,
                                totalAppsToScan = event.totalCount
                            )
                        }
                    }
                    is MalwareScanner.ScanEvent.Completed -> {
                        val deviceStatus = engine.deviceSecurityChecker.checkDeviceSecurity()
                        val privacyGroups = engine.auditPrivacyGroups(event.apps)
                        val report = engine.generateSecurityReport(
                            scannedApps = event.apps,
                            deviceStatus = deviceStatus,
                            isRealTimeProtectionActive = _uiState.value.isRealTimeProtectionEnabled,
                            lastScanTime = System.currentTimeMillis()
                        )

                        _uiState.update {
                            it.copy(
                                isScanning = false,
                                scanProgress = 1f,
                                currentScanningApp = "",
                                installedApps = event.apps,
                                filteredApps = applyFilter(event.apps, it.searchQuery, it.filterRiskLevel),
                                securityReport = report,
                                deviceSecurityStatus = deviceStatus,
                                privacyGroups = privacyGroups
                            )
                        }

                        engine.logScanCompleted(
                            scannedCount = event.scannedCount,
                            threatsCount = event.threatsCount,
                            scanType = if (isQuickScan) "কুইক স্ক্যান" else "সম্পূর্ণ ফুল স্ক্যান"
                        )
                    }
                    is MalwareScanner.ScanEvent.Error -> {
                        _uiState.update {
                            it.copy(
                                isScanning = false,
                                currentScanningApp = ""
                            )
                        }
                    }
                }
            }
        }
    }

    fun cancelScan() {
        scanJob?.cancel()
        _uiState.update { it.copy(isScanning = false, currentScanningApp = "") }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredApps = applyFilter(it.installedApps, query, it.filterRiskLevel)
            )
        }
    }

    fun updateFilterRiskLevel(riskLevel: RiskLevel?) {
        _uiState.update {
            it.copy(
                filterRiskLevel = riskLevel,
                filteredApps = applyFilter(it.installedApps, it.searchQuery, riskLevel)
            )
        }
    }

    private fun applyFilter(
        apps: List<AppSecurityInfo>,
        query: String,
        riskLevel: RiskLevel?
    ): List<AppSecurityInfo> {
        return apps.filter { app ->
            val matchesQuery = query.isBlank() ||
                    app.appName.contains(query, ignoreCase = true) ||
                    app.packageName.contains(query, ignoreCase = true)
            val matchesRisk = riskLevel == null || app.riskLevel == riskLevel
            matchesQuery && matchesRisk
        }
    }

    fun selectAppDetail(app: AppSecurityInfo?) {
        _uiState.update { it.copy(selectedAppForDetail = app) }
    }

    fun uninstallApp(packageName: String) {
        engine.threatResponseManager.startUninstallFlow(packageName)
    }

    fun openAppSettings(packageName: String) {
        engine.threatResponseManager.openAppSystemSettings(packageName)
    }

    fun whitelistApp(app: AppSecurityInfo) {
        viewModelScope.launch {
            engine.threatResponseManager.whitelistApp(app.packageName, app.appName)
            // Update local list
            val updated = _uiState.value.installedApps.map {
                if (it.packageName == app.packageName) it.copy(isWhitelisted = true, riskLevel = RiskLevel.SAFE) else it
            }
            _uiState.update {
                it.copy(
                    installedApps = updated,
                    filteredApps = applyFilter(updated, it.searchQuery, it.filterRiskLevel),
                    selectedAppForDetail = it.selectedAppForDetail?.copy(isWhitelisted = true, riskLevel = RiskLevel.SAFE)
                )
            }
        }
    }

    fun quarantineApp(app: AppSecurityInfo) {
        viewModelScope.launch {
            engine.threatResponseManager.quarantineApp(
                packageName = app.packageName,
                appName = app.appName,
                riskLevel = app.riskLevel.name,
                reasons = app.riskReasons.joinToString("; ")
            )
            val updated = _uiState.value.installedApps.map {
                if (it.packageName == app.packageName) it.copy(isQuarantined = true) else it
            }
            _uiState.update {
                it.copy(
                    installedApps = updated,
                    filteredApps = applyFilter(updated, it.searchQuery, it.filterRiskLevel),
                    selectedAppForDetail = it.selectedAppForDetail?.copy(isQuarantined = true)
                )
            }
        }
    }

    fun updateUrlInput(url: String) {
        _uiState.update { it.copy(urlInput = url) }
    }

    fun scanUrl(urlToScan: String? = null) {
        val target = urlToScan ?: _uiState.value.urlInput
        if (target.isBlank()) return

        _uiState.update { it.copy(isScanningUrl = true, urlInput = target) }
        viewModelScope.launch {
            val result = URLScanner.scanUrl(target)
            _uiState.update { it.copy(urlScanResult = result, isScanningUrl = false) }

            engine.database.securityEventDao().insertEvent(
                SecurityEventEntity(
                    eventType = "URL_SCANNED",
                    title = if (result.isSafe) "ওয়েব লিংক নিরাপদ: ${result.host}" else "⚠️ ক্ষতিকর ওয়েব লিংক শনাক্ত: ${result.host}",
                    details = "ইউআরএল: ${result.url}. ঝুঁকি: ${result.riskLevel.titleBn}. কারণ: ${result.indicators.joinToString(", ")}",
                    severity = if (result.isSafe) "INFO" else "DANGER"
                )
            )
        }
    }

    fun scanApkFile(file: File) {
        _uiState.update { it.copy(isScanningApk = true, apkScanError = null, apkScanResult = null) }
        viewModelScope.launch {
            try {
                val result = engine.apkScanner.scanApkFile(file)
                _uiState.update { it.copy(apkScanResult = result, isScanningApk = false) }

                engine.database.securityEventDao().insertEvent(
                    SecurityEventEntity(
                        eventType = "APK_SCANNED",
                        title = "APK ফাইল স্ক্যান সম্পন্ন: ${result.appName}",
                        details = "প্যাকেজ: ${result.packageName}. রেটিং: ${result.riskLevel.titleBn}",
                        severity = if (result.riskLevel == RiskLevel.SAFE) "INFO" else "WARNING"
                    )
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isScanningApk = false,
                        apkScanError = e.localizedMessage ?: "APK স্ক্যান করতে ব্যর্থ হয়েছে"
                    )
                }
            }
        }
    }

    fun scanCurrentAppAsApk() {
        // Handy feature allowing user to test the APK scanner directly on MrRashed BD's own APK file!
        val apkFile = File(getApplication<Application>().applicationInfo.sourceDir)
        if (apkFile.exists()) {
            scanApkFile(apkFile)
        } else {
            _uiState.update { it.copy(apkScanError = "APK সোর্স পাথ পাওয়া যায়নি") }
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            engine.database.securityEventDao().clearAllEvents()
        }
    }
}
