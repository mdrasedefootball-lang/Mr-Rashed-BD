package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.RiskLevel
import com.example.security.AppRiskAnalyzer
import com.example.security.ThreatIntelligence
import com.example.security.URLScanner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MrRashed BD", appName)
    }

    @Test
    fun `url scanner identifies phishing keyword and blacklisted host`() {
        val phishingUrl = "http://phishing-test-bank.xyz/login-verify"
        val result = URLScanner.scanUrl(phishingUrl)
        assertFalse(result.isSafe)
        assertTrue(result.riskLevel == RiskLevel.MALWARE_DETECTED || result.riskLevel == RiskLevel.HIGH_RISK)
    }

    @Test
    fun `url scanner marks legitimate https website as safe`() {
        val safeUrl = "https://www.google.com"
        val result = URLScanner.scanUrl(safeUrl)
        assertTrue(result.isSafe)
        assertEquals(RiskLevel.SAFE, result.riskLevel)
    }

    @Test
    fun `risk analyzer flags apps with dangerous permission combinations`() {
        val result = AppRiskAnalyzer.evaluate(
            packageName = "com.suspicious.stalker",
            isSystemApp = false,
            installationSource = "Sideloaded / Browser Download",
            permissions = listOf(
                "android.permission.RECEIVE_SMS",
                "android.permission.READ_SMS",
                "android.permission.INTERNET",
                "android.permission.BIND_ACCESSIBILITY_SERVICE"
            ),
            isDebuggable = true,
            certificateSha256 = "TEST_CERT_HASH",
            exportedComponentsCount = 5
        )

        assertTrue(result.riskLevel == RiskLevel.HIGH_RISK || result.riskLevel == RiskLevel.MALWARE_DETECTED)
        assertTrue(result.riskScore > 50)
    }
}
