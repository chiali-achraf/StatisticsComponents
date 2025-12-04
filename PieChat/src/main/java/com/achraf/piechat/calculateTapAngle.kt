package com.achraf.piechat

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan2

fun calculateTapAngle(center: Offset, tapOffset: Offset): Float {
    return (-atan2(
        x = center.y - tapOffset.y,
        y = center.x - tapOffset.x
    ) * (180f / PI).toFloat() - 90f).mod(360f)
}