package com.achraf.linechart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt


/**
 * Internal composable for rendering the chart content
 */
@Composable
fun MultiLineChartContent(
    lineData: List<LineChartData>,
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle(),
    unit: String = "",
    selectedDataPoint: DataPoint? = null,
    selectedLineIndex: Int = 0,
    onSelectedDataPoint: (DataPoint, Int) -> Unit = { _, _ -> },
    showHelperLines: Boolean = true,
    valueLabelConfig: ValueLabelConfig = ValueLabelConfig(),
    enableAnimation: Boolean = true
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = style.labelFontSize,
        color = style.textColor // Add text color
    )

    val referenceDataPoints = lineData.first().dataPoints

    // Get all data points from all lines for proper scaling
    val allDataPoints = remember(lineData) {
        lineData.flatMap { it.dataPoints }
    }

    val maxYValue = remember(allDataPoints) {
        allDataPoints.maxOfOrNull { it.y } ?: 0f
    }
    val minYValue = remember(allDataPoints) {
        allDataPoints.minOfOrNull { it.y } ?: 0f
    }

    val measurer = rememberTextMeasurer()
    var xLabelWidth by remember { mutableFloatStateOf(0f) }

    val selectedDataPointIndex = remember(selectedDataPoint, referenceDataPoints) {
        referenceDataPoints.indexOf(selectedDataPoint)
    }

    var drawPointsList by remember { mutableStateOf(listOf<List<DataPoint>>()) }
    var isShowingDataPoints by remember { mutableStateOf(selectedDataPoint != null) }

    // Animation for line drawing
    val animationProgress by animateFloatAsState(
        targetValue = if (enableAnimation) 1f else 1f,
        animationSpec = tween(
            durationMillis = style.animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "lineChartAnimation"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(style.backgroundColor)
            .pointerInput(drawPointsList, xLabelWidth) {
                detectTapGestures { offset ->
                    if (drawPointsList.isEmpty()) return@detectTapGestures

                    val tappedIndex = getSelectedDataPointIndex(
                        touchOffsetX = offset.x,
                        triggerWidth = xLabelWidth,
                        drawPoints = drawPointsList.first()
                    )

                    if (tappedIndex >= 0 && tappedIndex < referenceDataPoints.size) {
                        isShowingDataPoints = true

                        // Find which line is closest to tap
                        val tapY = offset.y
                        var closestLineIndex = 0
                        var minDistance = Float.MAX_VALUE

                        drawPointsList.forEachIndexed { lineIndex, points ->
                            if (tappedIndex < points.size) {
                                val point = points[tappedIndex]
                                val distance = kotlin.math.abs(point.y - tapY)
                                if (distance < minDistance) {
                                    minDistance = distance
                                    closestLineIndex = lineIndex
                                }
                            }
                        }

                        onSelectedDataPoint(
                            referenceDataPoints[tappedIndex],
                            closestLineIndex
                        )
                    }
                }
            }
    ) {
        val minLabelSpacingYPx = style.minYLabelSpacing.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()
        val horizontalPaddingPx = style.horizontalPadding.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()

        // X-axis label measurements
        val xLabelTextLayoutResults = referenceDataPoints.map {
            measurer.measure(
                text = it.xLabel,
                style = textStyle.copy(
                    textAlign = TextAlign.Center,
                    color = style.textColor // Add text color
                )
            )
        }
        val maxXLabelWidth = xLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0
        val maxXLabelHeight = xLabelTextLayoutResults.maxOfOrNull { it.size.height } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResults.maxOfOrNull { it.lineCount } ?: 0
        val xLabelLineHeight = if (maxXLabelLineCount > 0) {
            maxXLabelHeight / maxXLabelLineCount
        } else 0

        val viewPortHeightPx = size.height -
                (maxXLabelHeight + 2 * verticalPaddingPx +
                        xLabelLineHeight + xAxisLabelSpacingPx)

        // Y-axis label calculation
        val labelViewPortHeightPx = viewPortHeightPx + xLabelLineHeight
        val labelCountExcludingLastLabel =
            ((labelViewPortHeightPx / (xLabelLineHeight + minLabelSpacingYPx))).toInt()

        val valueIncrement = (maxYValue - minYValue) / labelCountExcludingLastLabel.coerceAtLeast(1)

        val yLabels = (0..labelCountExcludingLastLabel).map {
            ValueLabel(
                value = maxYValue - (valueIncrement * it),
                unit = unit,
                config = valueLabelConfig
            )
        }

        val yLabelTextLayoutResults = yLabels.map {
            measurer.measure(
                text = it.formatted(),
                style = textStyle.copy(color = style.textColor) // Add text color
            )
        }
        val maxYLabelWidth = yLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0

        // Viewport boundaries
        val viewPortTopY = verticalPaddingPx + xLabelLineHeight + 10f
        val viewPortRightX = size.width
        val viewPortBottomY = viewPortTopY + viewPortHeightPx
        val viewPortLeftX = 2f * horizontalPaddingPx + maxYLabelWidth

        xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx

        // Draw X-axis labels
        xLabelTextLayoutResults.forEachIndexed { index, result ->
            val x = viewPortLeftX + xAxisLabelSpacingPx / 2f +
                    xLabelWidth * index

            val isSelected = index == selectedDataPointIndex

            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = viewPortBottomY + xAxisLabelSpacingPx
                ),
                color = if (isSelected) {
                    lineData.getOrNull(selectedLineIndex)?.color ?: style.textColor // Use textColor
                } else style.textColor // Use textColor
            )

            if (showHelperLines) {
                drawLine(
                    color = if (isSelected) {
                        lineData.getOrNull(selectedLineIndex)?.color ?: style.textColor // Use textColor
                    } else style.textColor.copy(alpha = 0.3f), // Slightly transparent for helper lines
                    start = Offset(
                        x = x + result.size.width / 2f,
                        y = viewPortBottomY
                    ),
                    end = Offset(
                        x = x + result.size.width / 2f,
                        y = viewPortTopY
                    ),
                    strokeWidth = if (isSelected) {
                        style.helperLinesThicknessPx * 1.8f
                    } else style.helperLinesThicknessPx
                )
            }

            // Draw selected value label
            if (isSelected && selectedLineIndex < lineData.size) {
                val selectedLine = lineData[selectedLineIndex]
                if (index < selectedLine.dataPoints.size) {
                    val valueLabel = ValueLabel(
                        value = selectedLine.dataPoints[index].y,
                        unit = unit,
                        config = valueLabelConfig
                    )
                    val labelText = if (selectedLine.label.isNotEmpty()) {
                        "${selectedLine.label}: ${valueLabel.formatted()}"
                    } else {
                        valueLabel.formatted()
                    }

                    val valueResult = measurer.measure(
                        text = labelText,
                        style = textStyle.copy(
                            color = selectedLine.color
                        ),
                        maxLines = 1
                    )
                    val textPositionX = if (index == referenceDataPoints.lastIndex) {
                        x - valueResult.size.width
                    } else {
                        x - valueResult.size.width / 2f
                    } + result.size.width / 2f

                    val isTextInVisibleRange =
                        (size.width - textPositionX).roundToInt() in 0..size.width.roundToInt()

                    if (isTextInVisibleRange) {
                        drawText(
                            textLayoutResult = valueResult,
                            topLeft = Offset(
                                x = textPositionX.coerceAtLeast(0f),
                                y = viewPortTopY - valueResult.size.height - 10f
                            )
                        )
                    }
                }
            }
        }

        // Draw Y-axis labels
        val heightRequiredForLabels = xLabelLineHeight *
                (labelCountExcludingLastLabel + 1)
        val remainingHeightForLabels = labelViewPortHeightPx - heightRequiredForLabels
        val spaceBetweenLabels = if (labelCountExcludingLastLabel > 0) {
            remainingHeightForLabels / labelCountExcludingLastLabel
        } else 0f

        yLabelTextLayoutResults.forEachIndexed { index, result ->
            val x = horizontalPaddingPx + maxYLabelWidth - result.size.width.toFloat()
            val y = viewPortTopY +
                    index * (xLabelLineHeight + spaceBetweenLabels) -
                    xLabelLineHeight / 2f
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = y
                ),
                color = style.textColor // Use textColor
            )

            if (showHelperLines) {
                drawLine(
                    color = style.textColor.copy(alpha = 0.3f), // Slightly transparent for helper lines
                    start = Offset(
                        x = viewPortLeftX,
                        y = y + result.size.height.toFloat() / 2f
                    ),
                    end = Offset(
                        x = viewPortRightX,
                        y = y + result.size.height.toFloat() / 2f
                    ),
                    strokeWidth = style.helperLinesThicknessPx
                )
            }
        }

        // Calculate draw points for all lines
        drawPointsList = lineData.map { line ->
            line.dataPoints.mapIndexed { idx, dataPoint ->
                val x = viewPortLeftX + idx * xLabelWidth + xLabelWidth / 2f
                val ratio = if (maxYValue != minYValue) {
                    (dataPoint.y - minYValue) / (maxYValue - minYValue)
                } else 0.5f
                val y = viewPortBottomY - (ratio * viewPortHeightPx)
                DataPoint(
                    x = x,
                    y = y,
                    xLabel = dataPoint.xLabel
                )
            }
        }

        // Draw all lines
        drawPointsList.forEachIndexed { lineIndex, drawPoints ->
            val lineColor = lineData[lineIndex].color

            if (drawPoints.size < 2) return@forEachIndexed

            // Calculate control points for smooth curve
            val conPoints1 = mutableListOf<DataPoint>()
            val conPoints2 = mutableListOf<DataPoint>()
            for (i in 1 until drawPoints.size) {
                val p0 = drawPoints[i - 1]
                val p1 = drawPoints[i]

                val x = (p1.x + p0.x) / 2f
                val y1 = p0.y
                val y2 = p1.y

                conPoints1.add(DataPoint(x, y1, ""))
                conPoints2.add(DataPoint(x, y2, ""))
            }

            // Draw animated line
            val linePath = Path().apply {
                if (drawPoints.isNotEmpty()) {
                    moveTo(drawPoints.first().x, drawPoints.first().y)

                    val visiblePointCount = (drawPoints.size * animationProgress).toInt()
                        .coerceAtMost(drawPoints.size)

                    for (i in 1 until visiblePointCount) {
                        cubicTo(
                            x1 = conPoints1[i - 1].x,
                            y1 = conPoints1[i - 1].y,
                            x2 = conPoints2[i - 1].x,
                            y2 = conPoints2[i - 1].y,
                            x3 = drawPoints[i].x,
                            y3 = drawPoints[i].y
                        )
                    }
                }
            }

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(
                    width = style.lineStrokeWidth,
                    cap = StrokeCap.Round
                )
            )

            // Draw data point circles
            drawPoints.forEachIndexed { index, point ->
                if (isShowingDataPoints && animationProgress > 0.8f) {
                    val circleOffset = Offset(
                        x = point.x,
                        y = point.y
                    )

                    val isThisPointSelected = index == selectedDataPointIndex && selectedLineIndex == lineIndex

                    drawCircle(
                        color = lineColor.copy(alpha = if (isThisPointSelected) 1f else 0.7f),
                        radius = if (isThisPointSelected) style.selectedPointRadius * 1.2f else style.selectedPointRadius * 0.8f,
                        center = circleOffset
                    )

                    if (isThisPointSelected) {
                        drawCircle(
                            color = Color.White,
                            radius = style.selectedPointOuterRadius,
                            center = circleOffset
                        )
                        drawCircle(
                            color = lineColor,
                            radius = style.selectedPointOuterRadius,
                            center = circleOffset,
                            style = Stroke(width = 3f)
                        )
                    }
                }
            }
        }
    }
}

private fun getSelectedDataPointIndex(
    touchOffsetX: Float,
    triggerWidth: Float,
    drawPoints: List<DataPoint>
): Int {
    val triggerRangeLeft = touchOffsetX - triggerWidth / 2f
    val triggerRangeRight = touchOffsetX + triggerWidth / 2f
    return drawPoints.indexOfFirst {
        it.x in triggerRangeLeft..triggerRangeRight
    }
}