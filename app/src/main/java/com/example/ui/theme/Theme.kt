package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalMetroAccent = staticCompositionLocalOf { MetroAccent.COBALT }
val LocalIsMetroDark = staticCompositionLocalOf { true }

@Composable
fun MetroWeatherTheme(
    accent: MetroAccent = MetroAccent.COBALT,
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val accentColor = accent.color

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = MetroWhite,
            primaryContainer = accentColor,
            onPrimaryContainer = MetroWhite,
            secondary = accentColor,
            onSecondary = MetroWhite,
            tertiary = accentColor,
            background = MetroBlack,
            onBackground = MetroWhite,
            surface = MetroDarkGray,
            onSurface = MetroWhite,
            surfaceVariant = MetroMediumGray,
            onSurfaceVariant = MetroSubtleGray,
            outline = MetroLightGray
        )
    } else {
        lightColorScheme(
            primary = accentColor,
            onPrimary = MetroWhite,
            primaryContainer = accentColor,
            onPrimaryContainer = MetroWhite,
            secondary = accentColor,
            onSecondary = MetroWhite,
            tertiary = accentColor,
            background = MetroOffWhite,
            onBackground = MetroBlack,
            surface = MetroWhite,
            onSurface = MetroBlack,
            surfaceVariant = Color(0xFFE4E4E4),
            onSurfaceVariant = MetroDarkGray,
            outline = MetroLightGray
        )
    }

    CompositionLocalProvider(
        LocalMetroAccent provides accent,
        LocalIsMetroDark provides isDark
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MetroTypography,
            content = content
        )
    }
}
