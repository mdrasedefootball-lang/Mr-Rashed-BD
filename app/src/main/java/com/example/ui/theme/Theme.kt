package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MrRashedSecurityColorScheme = darkColorScheme(
    primary = CyberEmerald,
    onPrimary = CyberDarkBg,
    primaryContainer = CyberSurfaceCard,
    onPrimaryContainer = CyberEmerald,
    secondary = CyberCyan,
    onSecondary = CyberDarkBg,
    tertiary = CyberElectricBlue,
    background = CyberDarkBg,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceCard,
    onSurfaceVariant = TextSecondary,
    error = ThreatDanger,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MrRashedSecurityColorScheme,
        typography = Typography,
        content = content
    )
}
