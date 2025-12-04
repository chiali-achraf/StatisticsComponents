package com.achraf.linechart



import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Single Line Chart - Convenience wrapper for single line charts
 */
@Composable
fun LineChart(
    dataPoints: List<DataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF2196F3),
    lineLabel: String = "",
    style: ChartStyle = ChartStyle(),
    visibleDataPointsCount: Int? = null,
    unit: String = "",
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint) -> Unit = {},
    showHelperLines: Boolean = true,
    valueLabelConfig: ValueLabelConfig = ValueLabelConfig(),
    enableAnimation: Boolean = true,
    enableHorizontalScroll: Boolean = true
) {
    MultiLineChart(
        lineData = listOf(
            LineChartData(
                dataPoints = dataPoints,
                color = lineColor,
                label = lineLabel
            )
        ),
        modifier = modifier,
        style = style,
        visibleDataPointsCount = visibleDataPointsCount,
        unit = unit,
        selectedDataPoint = selectedDataPoint,
        selectedLineIndex = 0,
        onSelectedDataPoint = { point, _ -> onSelectedDataPoint(point) },
        showHelperLines = showHelperLines,
        valueLabelConfig = valueLabelConfig,
        enableAnimation = enableAnimation,
        enableHorizontalScroll = enableHorizontalScroll
    )
}