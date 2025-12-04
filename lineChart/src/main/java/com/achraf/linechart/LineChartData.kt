package com.achraf.linechart

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a complete line series
 * @param dataPoints List of data points for this line
 * @param color Color of the line
 * @param label Label/name for this data series
 */
data class LineChartData(
    val dataPoints: List<DataPoint>,
    val color: Color,
    val label: String = ""
)