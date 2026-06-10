package com.nailong.world.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NailongWarmOrange,
    onPrimary = Color.White,
    primaryContainer = NailongLightGold,
    onPrimaryContainer = NailongCharcoal,
    secondary = NailongSoftCoral,
    onSecondary = Color.White,
    tertiary = NailongWarmOrange,
    background = NailongYellow,
    onBackground = NailongCharcoal,
    surface = NailongCream,
    onSurface = NailongCharcoal,
    surfaceVariant = NailongCardBg,
    onSurfaceVariant = NailongWarmGray,
    outline = NailongCardStroke,
    outlineVariant = NailongCardStroke.copy(alpha = 0.5f),
)

private val DarkColorScheme = darkColorScheme(
    primary = NailongWarmOrange,
    onPrimary = Color.White,
    primaryContainer = NailongWarmOrange.copy(alpha = 0.2f),
    onPrimaryContainer = NailongLightGold,
    secondary = NailongSoftCoral,
    onSecondary = Color.White,
    background = NailongDarkBg,
    onBackground = NailongDarkText,
    surface = NailongDarkCard,
    onSurface = NailongDarkText,
    surfaceVariant = NailongDarkSurface,
    onSurfaceVariant = NailongDarkSecondary,
    outline = NailongDarkSurface,
)

@Composable
fun NailongWorldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NailongTypography,
        content = content
    )
}
