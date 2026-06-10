package com.nailong.world.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.basic.MiuixTheme
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.ThemeController

/**
 * Miuix-based theme with warm dragon-inspired colors.
 * Uses Monet dynamic color with a custom key color.
 */
@Composable
fun NailongWorldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val keyColor = Color(0xFFFF8C42) // Warm orange seed color
    val controller = remember {
        ThemeController(
            mode = if (darkTheme) ColorSchemeMode.MonetDark else ColorSchemeMode.MonetLight,
            keyColor = keyColor,
        )
    }
    MiuixTheme(
        controller = controller,
        content = content,
    )
}
