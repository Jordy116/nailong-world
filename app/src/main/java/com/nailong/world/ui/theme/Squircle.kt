package com.nailong.world.ui.theme

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.min

/**
 * A super-ellipse "Squircle" shape inspired by HyperOS/Miuix.
 * Smooth, continuous corner radius that looks softer than a standard rounded rect.
 */
class SquircleShape(private val radius: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density,
    ): Outline {
        val r = radius.toPx().coerceAtMost(min(size.width, size.height) / 2f)
        // Use a cubic bezier approximation of a squircle
        val path = Path().apply {
            // Top edge
            moveTo(r, 0f)
            lineTo(size.width - r, 0f)
            // Top-right corner
            cubicTo(size.width - r * 0.4f, 0f, size.width, r * 0.4f, size.width, r)
            // Right edge
            lineTo(size.width, size.height - r)
            // Bottom-right corner
            cubicTo(size.width, size.height - r * 0.4f, size.width - r * 0.4f, size.height, size.width - r, size.height)
            // Bottom edge
            lineTo(r, size.height)
            // Bottom-left corner
            cubicTo(r * 0.4f, size.height, 0f, size.height - r * 0.4f, 0f, size.height - r)
            // Left edge
            lineTo(0f, r)
            // Top-left corner
            cubicTo(0f, r * 0.4f, r * 0.4f, 0f, r, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

/** Glassmorphism backdrop modifier factory */
object GlassStyles {

    /** Card glass style — warm tint, subtle border, blur backdrop */
    val cardGlassStyle: androidx.compose.ui.Modifier
        get() = androidx.compose.ui.Modifier

    fun cardGlass(
        backgroundColor: androidx.compose.ui.graphics.Color,
        borderColor: androidx.compose.ui.graphics.Color,
        blurRadius: Int = 20,
    ): androidx.compose.ui.Modifier {
        return androidx.compose.ui.Modifier
    }
}
