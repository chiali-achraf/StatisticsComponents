package com.achraf.linechart


import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



/**
 * Multi-Line Interactive Chart Composable with Horizontal Scrolling
 *
 * @param lineData List of line series to display (can be single or multiple lines, each with its own color)
 * @param modifier Modifier for the chart container
 * @param style Chart styling configuration
 * @param visibleDataPointsCount Number of data points visible at once (null = all points visible)
 * @param unit Unit string to append to Y-axis values (e.g., "$", "kg", "%")
 * @param selectedDataPoint Currently selected data point
 * @param selectedLineIndex Index of the currently selected line
 * @param onSelectedDataPoint Callback when a data point is selected (DataPoint, lineIndex)
 * @param showHelperLines Whether to show grid helper lines
 * @param valueLabelConfig Configuration for value label formatting
 * @param enableAnimation Whether to enable entrance animation
 * @param enableHorizontalScroll Whether to enable horizontal scrolling
 */
@Composable
fun MultiLineChart(
    lineData: List<LineChartData>,
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle(),
    visibleDataPointsCount: Int? = null,
    unit: String = "",
    selectedDataPoint: DataPoint? = null,
    selectedLineIndex: Int = 0,
    onSelectedDataPoint: (DataPoint, Int) -> Unit = { _, _ -> },
    showHelperLines: Boolean = true,
    valueLabelConfig: ValueLabelConfig = ValueLabelConfig(),
    enableAnimation: Boolean = true,
    enableHorizontalScroll: Boolean = true
) {
    require(lineData.isNotEmpty()) { "Line data cannot be empty" }
    require(lineData.all { it.dataPoints.isNotEmpty() }) { "All line data must have at least one data point" }

    // Use the first line's data points for x-axis labels (all lines should have same x-axis)
    val referenceDataPoints = lineData.first().dataPoints
    val totalDataPoints = referenceDataPoints.size

    // Calculate visible range for scrolling
    val scrollState = rememberScrollState()
    val actualVisibleCount = visibleDataPointsCount ?: totalDataPoints

    val scrollModifier = if (enableHorizontalScroll && visibleDataPointsCount != null && totalDataPoints > actualVisibleCount) {
        Modifier.horizontalScroll(scrollState)
    } else {
        Modifier
    }

    // Calculate chart width for scrolling
    val chartWidth = if (enableHorizontalScroll && visibleDataPointsCount != null && totalDataPoints > actualVisibleCount) {
        (totalDataPoints * 60).dp
    } else {
        null
    }

    Box(modifier = modifier) {
        MultiLineChartContent(
            lineData = lineData,
            modifier = scrollModifier.then(
                if (chartWidth != null) {
                    Modifier.width(chartWidth)
                } else {
                    Modifier.fillMaxWidth()
                }
            ),
            style = style,
            unit = unit,
            selectedDataPoint = selectedDataPoint,
            selectedLineIndex = selectedLineIndex,
            onSelectedDataPoint = onSelectedDataPoint,
            showHelperLines = showHelperLines,
            valueLabelConfig = valueLabelConfig,
            enableAnimation = enableAnimation,
        )
    }
}
