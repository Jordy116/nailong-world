package com.nailong.world.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp

/**
 * Squircle shape — uses a super-ellipse approximation via bezier curves.
 * Falls back to RoundedCornerShape for simplicity.
 */
object SquircleShape {
    operator fun invoke(size: Dp) = RoundedCornerShape(size)
}
