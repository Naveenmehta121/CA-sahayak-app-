package com.casahayak.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Light Color Scheme ───────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    onPrimary = Neutral99,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo10,
    secondary = Gold50,
    onSecondary = Neutral99,
    secondaryContainer = Gold90,
    onSecondaryContainer = Gold10,
    tertiary = Indigo60,
    onTertiary = Neutral99,
    background = SurfaceLight,
    onBackground = Neutral10,
    surface = SurfaceLight,
    onSurface = Neutral10,
    surfaceVariant = Indigo95,
    onSurfaceVariant = Neutral40,
    outline = Neutral50,
    error = Error40,
    onError = Neutral99,
    errorContainer = Error90,
    onErrorContainer = Error10
)

// ─── Dark Color Scheme ────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo20,
    primaryContainer = Indigo30,
    onPrimaryContainer = Indigo90,
    secondary = Gold80,
    onSecondary = Gold20,
    secondaryContainer = Gold30,
    onSecondaryContainer = Gold90,
    tertiary = Indigo70,
    onTertiary = Indigo10,
    background = SurfaceDark,
    onBackground = Neutral90,
    surface = SurfaceDark,
    onSurface = Neutral90,
    surfaceVariant = Indigo20,
    onSurfaceVariant = Neutral70,
    outline = Neutral60,
    error = Error80,
    onError = Error10,
    errorContainer = Error40,
    onErrorContainer = Error90
)

/**
 * CA Sahayak Material 3 theme.
 * Automatically switches between light and dark based on system setting.
 */
@Composable
fun CaSahayakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
