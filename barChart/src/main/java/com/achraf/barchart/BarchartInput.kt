package com.achraf.barchart

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a single bar in the bar chart
 *
 * @param value The numerical value of the bar
 * @param description The label/description for the bar
 * @param color The primary color of the bar
 * @param textColor Optional text color (defaults to primary color if null)
 */
data class BarChartInput(
    val value: Int,
    val description: String,
    val color: Color,
    val textColor: Color? = null
)
