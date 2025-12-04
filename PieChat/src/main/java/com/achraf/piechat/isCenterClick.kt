package com.achraf.piechat

import androidx.compose.ui.geometry.Offset

fun isCenterClick(center: Offset, offset: Offset, innerRadius: Float, angle: Float): Boolean {
    return when {
        angle < 90 -> offset.x < center.x + innerRadius && offset.y < center.y + innerRadius
        angle < 180 -> offset.x > center.x - innerRadius && offset.y < center.y + innerRadius
        angle < 270 -> offset.x > center.x - innerRadius && offset.y > center.y - innerRadius
        else -> offset.x < center.x + innerRadius && offset.y > center.y - innerRadius
    }
}