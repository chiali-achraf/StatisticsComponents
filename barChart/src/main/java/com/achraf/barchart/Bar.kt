package com.achraf.barchart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.graphics.Paint
import kotlin.math.roundToInt


/**
 * Individual bar component that can render in 2D or 3D style
 *
 * @param modifier Modifier for the bar
 * @param primaryColor Primary color of the bar
 * @param percentage Percentage value (0.0 to 1.0)
 * @param description Description text for the bar
 * @param showDescription Whether to show the description
 * @param textColor Color for text labels
 * @param backgroundColor Background/shadow color for 3D effect
 * @param style Bar style (2D or 3D)
 * @param textSize Text size for labels
 */

@Composable
fun Bar(
    modifier: Modifier = Modifier,
    primaryColor: Color,
    percentage: Float,
    description: String,
    showDescription: Boolean,
    textColor: Color,
    backgroundColor: Color,
    style: BarChartStyle = BarChartStyle.PERSPECTIVE_3D,
    textSize: Dp = 14.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height

            when (style) {
                BarChartStyle.FLAT_2D -> draw2DBar(
                    width = width,
                    height = height,
                    primaryColor = primaryColor,
                    backgroundColor = backgroundColor
                )
                BarChartStyle.PERSPECTIVE_3D -> draw3DBar(
                    width = width,
                    height = height,
                    primaryColor = primaryColor,
                    backgroundColor = backgroundColor
                )
            }
        }
    }
}

private fun DrawScope.draw2DBar(
    width: Float,
    height: Float,
    primaryColor: Color,
    backgroundColor: Color
) {
    val barWidth = width * 0.8f
    val barLeft = (width - barWidth) / 2f

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(primaryColor, backgroundColor),
            startY = 0f,
            endY = height
        ),
        topLeft = Offset(barLeft, 0f),
        size = Size(barWidth, height)
    )
}

private fun DrawScope.draw3DBar(
    width: Float,
    height: Float,
    primaryColor: Color,
    backgroundColor: Color
) {
    val barWidth = width / 5 * 3
    val barHeight = height / 8 * 7
    val barHeight3DPart = height - barHeight
    val barWidth3DPart = (width - barWidth) * (height * 0.002f)

    // Front face
    var path = Path().apply {
        moveTo(0f, height)
        lineTo(barWidth, height)
        lineTo(barWidth, height - barHeight)
        lineTo(0f, height - barHeight)
        close()
    }
    drawPath(
        path,
        brush = Brush.linearGradient(
            colors = listOf(backgroundColor, primaryColor)
        )
    )

    // Side face
    path = Path().apply {
        moveTo(barWidth, height - barHeight)
        lineTo(barWidth3DPart + barWidth, 0f)
        lineTo(barWidth3DPart + barWidth, barHeight)
        lineTo(barWidth, height)
        close()
    }
    drawPath(
        path,
        brush = Brush.linearGradient(
            colors = listOf(primaryColor, backgroundColor)
        )
    )

    // Top face
    path = Path().apply {
        moveTo(0f, barHeight3DPart)
        lineTo(barWidth, barHeight3DPart)
        lineTo(barWidth + barWidth3DPart, 0f)
        lineTo(barWidth3DPart, 0f)
        close()
    }
    drawPath(
        path,
        brush = Brush.linearGradient(
            colors = listOf(backgroundColor, primaryColor)
        )
    )
}