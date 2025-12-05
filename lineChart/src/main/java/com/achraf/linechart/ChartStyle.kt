package com.achraf.linechart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * Configuration class for line chart styling
 */

data class ChartStyle(
    val backgroundColor: Color = Color.Transparent,
    val textColor: Color = Color(0xFF7C7C7C),
    val helperLinesThicknessPx: Float = 1f,
    val axisLinesThicknessPx: Float = 5f,
    val labelFontSize: TextUnit = 14.sp,
    val minYLabelSpacing: Dp = 25.dp,
    val verticalPadding: Dp = 8.dp,
    val horizontalPadding: Dp = 8.dp,
    val xAxisLabelSpacing: Dp = 8.dp,
    val lineStrokeWidth: Float = 5f,
    val selectedPointRadius: Float = 10f,
    val selectedPointOuterRadius: Float = 15f,
    val animationDuration: Int = 1000
)