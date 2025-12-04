package com.achraf.piechat

import androidx.compose.ui.graphics.Color


/**
 * Data class representing a segment in the Pie Chart.
 *
 * @param color Color of the segment
 * @param value Numerical value of the segment
 * @param description Description text for the segment
 * @param isTapped Whether the segment is currently tapped/selected
 */
data class PieChartInput(
    val color: Color,
    val value: Int,
    val description: String,
    val isTapped: Boolean = false
)
