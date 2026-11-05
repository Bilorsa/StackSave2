package com.example.stacksave.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),          // Royal Indigo / Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF059669),        // Cashback Emerald Green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = Color(0xFF7C3AED),         // Violet Accent
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = Color(0xFF5B21B6),
    background = Color(0xFFF8FAFC),       // Clean Slate 50
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFE2E8F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFEFF6FF),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFFECFDF5),
    tertiary = Color(0xFFA78BFA),
    onTertiary = Color(0xFF3730A3),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

@Composable
fun StackSaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColors = if (darkTheme) DarkColorScheme else LightColorScheme
    val colors = if (highContrast) {
        if (darkTheme) baseColors.copy(
            onBackground = Color.White,
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFFE8EDF5),
            outline = Color(0xFFB8C4D6)
        ) else baseColors.copy(
            onBackground = Color(0xFF000000),
            onSurface = Color(0xFF000000),
            onSurfaceVariant = Color(0xFF202020),
            outline = Color(0xFF555555)
        )
    } else baseColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
