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
    primary = NailongPrimary,
    onPrimary = Color.White,
    primaryContainer = NailongAccent,
    onPrimaryContainer = NailongText,
    secondary = NailongSecondary,
    onSecondary = Color.White,
    background = NailongBg,
    onBackground = NailongText,
    surface = NailongSurface,
    onSurface = NailongText,
    surfaceVariant = NailongCardBg,
    onSurfaceVariant = NailongTextSecondary,
    outline = NailongDivider,
)

private val DarkColorScheme = darkColorScheme(
    primary = NailongPrimary,
    onPrimary = Color.White,
    primaryContainer = NailongPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = NailongAccent,
    secondary = NailongSecondary,
    onSecondary = Color.White,
    background = NailongDarkBg,
    onBackground = NailongDarkText,
    surface = NailongDarkSurface,
    onSurface = NailongDarkText,
    surfaceVariant = NailongDarkCard,
    onSurfaceVariant = NailongDarkSecondary,
    outline = NailongDarkCard,
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
        content = content,
    )
}
