package com.achraf.barchart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.graphics.Paint
import kotlin.math.ceil

/**
 * A customizable Bar Chart component with axis markers
 *
 * @param inputList List of BarChartInput data to display
 * @param modifier Modifier for the Bar Chart
 * @param showDescription Whether to show description labels on X-axis
 * @param style The visual style of the bars (2D or 3D)
 * @param barWidth Width of each individual bar
 * @param maxBarHeight Maximum height of the tallest bar
 * @param spacing Space between bars
 * @param textSize Text size for labels
 * @param axisColor Color for axis lines and text
 * @param showYAxisValues Whether to show Y-axis value markers
 * @param yAxisSteps Number of steps/divisions on Y-axis
 * @param maxY Optional maximum value for Y-axis. If provided, it will be used as the reference maximum.
 *             If not provided, the chart will use the maximum value from input data.
 * @param topRadius Corner radius for the top of 2D bars
 * @param bottomRadius Corner radius for the bottom of 2D bars
 */
@Composable
fun BarChart(
    inputList: List<BarChartInput>,
    modifier: Modifier = Modifier,
    showDescription: Boolean = true,
    style: BarChartStyle = BarChartStyle.PERSPECTIVE_3D,
    barWidth: Dp = 40.dp,
    maxBarHeight: Dp = 120.dp,
    spacing: Dp = 8.dp,
    textSize: Dp = 14.dp,
    axisColor: Color = Color.Black,
    showYAxisValues: Boolean = true,
    yAxisSteps: Int = 5,
    maxY: Int? = null,
    topRadius: Float = 20f,
    bottomRadius: Float = 20f,
    enableHorizontalScroll: Boolean = true
) {
    require(inputList.isNotEmpty()) { "Input list cannot be empty" }
    require(inputList.all { it.value >= 0 }) { "All values must be non-negative" }

    val maxValue = remember(inputList) {
        val max = inputList.maxOf { it.value }
        if (max == 0) 100 else max // Default to 100 if all values are 0
    }

    val yAxisMax = remember(maxValue, yAxisSteps, maxY) {
        // If maxY is provided, use it as the maximum
        val referenceMax = maxY ?: maxValue

        // Calculate a nice round number for the Y-axis maximum
        when {
            referenceMax == 0 -> yAxisSteps * 20 // Default scale if all zero
            referenceMax <= 10 -> yAxisSteps * 2
            referenceMax <= 50 -> {
                val step = ceil(referenceMax.toFloat() / yAxisSteps / 10).toInt() * 10
                step * yAxisSteps
            }
            referenceMax <= 100 -> {
                val step = ceil(referenceMax.toFloat() / yAxisSteps / 20).toInt() * 20
                step * yAxisSteps
            }
            referenceMax <= 1000 -> {
                val step = ceil(referenceMax.toFloat() / yAxisSteps / 50).toInt() * 50
                step * yAxisSteps
            }
            referenceMax <= 10000 -> {
                val step = ceil(referenceMax.toFloat() / yAxisSteps / 500).toInt() * 500
                step * yAxisSteps
            }
            else -> {
                val step = ceil(referenceMax.toFloat() / yAxisSteps / 1000).toInt() * 1000
                step * yAxisSteps
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
    ) {
        Spacer(modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            // Y-axis with values
            if (showYAxisValues) {
                YAxis(
                    maxValue = yAxisMax,
                    steps = yAxisSteps,
                    height = maxBarHeight,
                    axisColor = axisColor,
                    textSize = textSize,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Bars with horizontal scroll
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier
                    .then(
                        if (enableHorizontalScroll) {
                            Modifier.horizontalScroll(scrollState)
                        } else {
                            Modifier
                        }
                    )
                    .padding(start = if (showYAxisValues) 0.dp else 16.dp)
            ) {
                inputList.forEach { input ->
                    // Calculate height based on actual value relative to Y-axis maximum
                    val calculatedHeight = if (input.value > 0) {
                        maxBarHeight * (input.value.toFloat() / yAxisMax)
                    } else {
                        0.dp // Show no bar for zero values
                    }
                    val percentage = if (yAxisMax > 0) input.value.toFloat() / yAxisMax else 0f

                    Bar(
                        modifier = Modifier
                            .height(calculatedHeight)
                            .width(barWidth),
                        primaryColor = input.color,
                        percentage = percentage,
                        description = input.description,
                        showDescription = false,
                        textColor = input.textColor ?: input.color,
                        backgroundColor = input.color.copy(alpha = 0.5f),
                        style = style,
                        textSize = textSize,
                    )
                }
            }
        }

        // X-axis with rotated descriptions
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            if (showYAxisValues) {
                Spacer(modifier = Modifier.width(58.dp))
            }

            if (showDescription) {
                Row(
                    modifier = Modifier
                        .then(
                            if (enableHorizontalScroll) {
                                Modifier.horizontalScroll(scrollState)
                            } else {
                                Modifier
                            }
                        )
                        .padding(start = if (showYAxisValues) 0.dp else 16.dp)
                ) {
                    XAxisContent(
                        inputList = inputList,
                        barWidth = barWidth,
                        spacing = spacing,
                        axisColor = axisColor,
                        textSize = textSize
                    )
                }
            }
        }
    }
}

@Composable
private fun YAxis(
    maxValue: Int,
    steps: Int,
    height: Dp,
    axisColor: Color,
    textSize: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(50.dp)
            .height(height),
        contentAlignment = Alignment.CenterEnd
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasHeight = size.height
            val stepHeight = canvasHeight / steps
            val stepValue = maxValue / steps

            // Draw axis line
            drawLine(
                color = axisColor,
                start = Offset(size.width, 0f),
                end = Offset(size.width, canvasHeight),
                strokeWidth = 2.dp.toPx()
            )

            // Draw value markers
            for (i in 0..steps) {
                val y = canvasHeight - (i * stepHeight)
                val value = i * stepValue

                // Tick mark
                drawLine(
                    color = axisColor,
                    start = Offset(size.width - 5.dp.toPx(), y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.dp.toPx()
                )

                // Value text
                drawContext.canvas.nativeCanvas.drawText(
                    value.toString(),
                    size.width - 10.dp.toPx(),
                    y + 5.dp.toPx(),
                    Paint().apply {
                        color = axisColor.toArgb()
                        this.textSize = textSize.toPx()
                        textAlign = Paint.Align.RIGHT
                    }
                )
            }
        }
    }
}

@Composable
private fun XAxisContent(
    inputList: List<BarChartInput>,
    barWidth: Dp,
    spacing: Dp,
    axisColor: Color,
    textSize: Dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        inputList.forEach { input ->
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(60.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2



                    // Draw rotated text
                    rotate(degrees = 90f, pivot = Offset(centerX, 12.dp.toPx())) {
                        drawContext.canvas.nativeCanvas.drawText(
                            input.description,
                            centerX,
                            12.dp.toPx() + textSize.toPx() / 3,
                            Paint().apply {
                                color = (input.textColor ?: input.color).toArgb()
                                this.textSize = textSize.toPx()
                                textAlign = Paint.Align.LEFT
                            }
                        )
                    }
                }
            }
        }
    }
}
