package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * PROJECT X — GLOBAL PROPERTY GROUP
 * Material Design 3 Theme Definition
 *
 * Core Tonal Hierarchy:
 * - Primary: Deep Navy (Formal corporate anchor)
 * - Secondary: Charcoal (Architectural depth & structure)
 * - Tertiary / Accents: Champagne Gold (High-value highlights, badges, prices)
 */

private val DarkColorScheme = darkColorScheme(
    primary = ProjectXChampagneGold,
    onPrimary = ProjectXNavyDeep,
    primaryContainer = ProjectXNavyLight,
    onPrimaryContainer = ProjectXSand,
    secondary = ProjectXCharcoalLight,
    onSecondary = Color.White,
    secondaryContainer = ProjectXCharcoalDark,
    onSecondaryContainer = ProjectXSand,
    tertiary = ProjectXChampagneGoldLight,
    onTertiary = ProjectXNavyDeep,
    tertiaryContainer = ProjectXChampagneGoldContainer,
    onTertiaryContainer = ProjectXChampagneGoldLight,
    background = ProjectXDarkBackground,
    onBackground = Color.White,
    surface = ProjectXDarkSurface,
    onSurface = Color.White,
    surfaceVariant = ProjectXNavyLight,
    onSurfaceVariant = Color(0xFFCAD5E2),
    outline = ProjectXDarkBorder,
    outlineVariant = ProjectXNavySubtle
)

private val LightColorScheme = lightColorScheme(
    primary = ProjectXNavy,
    onPrimary = Color.White,
    primaryContainer = ProjectXNavyLight,
    onPrimaryContainer = Color.White,
    secondary = ProjectXCharcoal,
    onSecondary = Color.White,
    secondaryContainer = ProjectXCharcoalLight,
    onSecondaryContainer = Color.White,
    tertiary = ProjectXChampagneGold,
    onTertiary = ProjectXNavyDeep,
    tertiaryContainer = ProjectXChampagneGoldMuted,
    onTertiaryContainer = ProjectXChampagneGoldDark,
    background = ProjectXBackground,
    onBackground = ProjectXTextPrimary,
    surface = ProjectXSurface,
    onSurface = ProjectXTextPrimary,
    surfaceVariant = ProjectXSurfaceVariant,
    onSurfaceVariant = ProjectXTextSecondary,
    outline = ProjectXBorder,
    outlineVariant = ProjectXBorder.copy(alpha = 0.5f)
)

@Composable
fun ProjectXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve bespoke PROJECT X formal luxury branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward-compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ProjectXTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
