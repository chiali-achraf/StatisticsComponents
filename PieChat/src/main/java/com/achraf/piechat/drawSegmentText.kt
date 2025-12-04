package com.achraf.piechat

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import android.graphics.Paint
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp

fun DrawScope.drawSegmentText(
    pieChartInput: PieChartInput,
    totalValue: Float,
    currentStartAngle: Float,
    angleToDraw: Float,
    circleCenter: Offset,
    radius: Float,
    innerRadius: Float,
    textColor: Color
) {
    var rotateAngle = currentStartAngle + angleToDraw / 2f - 90f
    var factor = 1f

    if (rotateAngle > 90f) {
        rotateAngle = (rotateAngle + 180).mod(360f)
        factor = -0.92f
    }

    val percentage = (pieChartInput.value / totalValue * 100).toInt()

    drawContext.canvas.nativeCanvas.apply {
        if (percentage > 3) {
            // Note: You can't use rotate() here directly on nativeCanvas
            // You need to use the DrawScope's rotate function
            this@drawSegmentText.rotate(rotateAngle) {
                drawText(
                    "$percentage %",
                    circleCenter.x,
                    circleCenter.y + (radius - (radius - innerRadius) / 2f) * factor,
                    Paint().apply {
                        textSize = 13.sp.toPx()
                        textAlign = Paint.Align.CENTER
                        color = textColor.toArgb()
                    }
                )
            }
        }
    }
}