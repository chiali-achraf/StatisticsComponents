package com.achraf.linechart


/**
 * Data class representing a point on the line chart
 * @param x The x-coordinate value (used for positioning)
 * @param y The y-coordinate value (displayed on chart)
 * @param xLabel The label to display on x-axis
 */
data class DataPoint(
    val x: Float,
    val y: Float,
    val xLabel: String
)