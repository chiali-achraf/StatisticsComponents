package com.achraf.piechat

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import android.graphics.Paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp

fun DrawScope.drawSegmentHighlight(
pieChartInput: PieChartInput,
currentStartAngle: Float,
angleToDraw: Float,
circleCenter: Offset,
radius: Float,
highlightColor: Color,
textColor: Color
) {
    val tabRotation = currentStartAngle - 90f
    val textRotation = currentStartAngle + angleToDraw / 2f - 90f
    var factor = 1f
    var adjustedTextRotation = textRotation

    if (textRotation > 90f) {
        adjustedTextRotation = (textRotation + 180).mod(360f)
        factor = -0.92f
    }

    // Draw highlight bars
    rotate(tabRotation) {
        drawRoundRect(
            topLeft = circleCenter,
            size = Size(12f, radius * 1.2f),
            color = highlightColor,
            cornerRadius = CornerRadius(15f, 15f)
        )
    }
    rotate(tabRotation + angleToDraw) {
        drawRoundRect(
            topLeft = circleCenter,
            size = Size(12f, radius * 1.2f),
            color = highlightColor,
            cornerRadius = CornerRadius(15f, 15f)
        )
    }

    // Draw description text
    rotate(adjustedTextRotation) {
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "${pieChartInput.description}: ${pieChartInput.value}",
                circleCenter.x,
                circleCenter.y + radius * 1.3f * factor,
                Paint().apply {
                    textSize = 22.sp.toPx()
                    textAlign = Paint.Align.CENTER
                    color = textColor.toArgb()
                    isFakeBoldText = true
                }
            )
        }
    }
}