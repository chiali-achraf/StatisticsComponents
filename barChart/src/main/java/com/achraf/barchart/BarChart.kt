package com.achraf.barchart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
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
    maxY: Int? = null
) {
    require(inputList.isNotEmpty()) { "Input list cannot be empty" }
    require(inputList.all { it.value > 0 }) { "All values must be positive" }

    val maxValue = remember(inputList) {
        inputList.maxOf { it.value }
    }

    val yAxisMax = remember(maxValue, yAxisSteps, maxY) {
        // If maxY is provided, use it as the maximum
        val referenceMax = maxY ?: maxValue

        // Calculate steps based on the reference maximum
        val step = ceil(referenceMax.toFloat() / yAxisSteps).toInt()
        step * yAxisSteps
    }

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

            // Bars
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.padding(start = if (showYAxisValues) 0.dp else 16.dp)
            ) {
                inputList.forEach { input ->
                    // Calculate height based on actual value relative to Y-axis maximum
                    val calculatedHeight = maxBarHeight * (input.value.toFloat() / yAxisMax)
                    val percentage = input.value / 100f // For display purposes

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
                        textSize = textSize
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
                XAxis(
                    inputList = inputList,
                    barWidth = barWidth,
                    spacing = spacing,
                    axisColor = axisColor,
                    textSize = textSize,
                    modifier = Modifier.padding(start = if (showYAxisValues) 0.dp else 16.dp)
                )
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
private fun XAxis(
    inputList: List<BarChartInput>,
    barWidth: Dp,
    spacing: Dp,
    axisColor: Color,
    textSize: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // X-axis horizontal line
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        ) {
//            drawLine(
//                color = axisColor,
//                start = Offset(0f, 0f),
//                end = Offset(size.width, 0f),
//                strokeWidth = 2.dp.toPx()
//            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Description labels
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

//                        // Draw tick mark
//                        drawLine(
//                            color = axisColor,
//                            start = Offset(centerX, 0f),
//                            end = Offset(centerX, 8.dp.toPx()),
//                            strokeWidth = 2.dp.toPx()
//                        )

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
}