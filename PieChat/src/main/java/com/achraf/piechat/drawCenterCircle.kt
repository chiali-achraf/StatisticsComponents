package com.achraf.piechat

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import android.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

fun DrawScope.drawCenterCircle(
    circleCenter: Offset,
    innerRadius: Float,
    transparentWidth: Float
) {
    drawContext.canvas.nativeCanvas.apply {
        drawCircle(
            circleCenter.x,
            circleCenter.y,
            innerRadius,
            Paint().apply {
                color = Color.White.copy(alpha = 0.6f).toArgb()
                setShadowLayer(10f, 0f, 0f, Color.Gray.toArgb())
            }
        )
    }

    drawCircle(
        color = Color.White.copy(0.2f),
        center = circleCenter,
        radius = innerRadius + transparentWidth / 2f
    )
}