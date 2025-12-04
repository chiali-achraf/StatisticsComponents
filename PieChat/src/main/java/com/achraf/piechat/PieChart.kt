package com.achraf.piechat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * A customizable animated Pie Chart component for Jetpack Compose.
 *
 * @param modifier Modifier for the Pie Chart
 * @param input List of PieChartInput data to display
 * @param radius Outer radius of the pie chart
 * @param innerRadius Inner radius for the center cutout
 * @param transparentWidth Width of the transparent ring around the center
 * @param centerText Text to display in the center of the chart
 * @param textColor Color for percentage and description text
 * @param centerTextColor Color for the center text
 * @param highlightColor Color for the highlight effect when a segment is tapped
 * @param animationDuration Duration of animations in milliseconds
 * @param enableAnimations Whether to enable animations
 */
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    input: List<PieChartInput>,
    radius: Float = 500f,
    innerRadius: Float = 250f,
    transparentWidth: Float = 70f,
    centerText: String? = null,
    textColor: Color = Color.White,
    centerTextColor: Color = Color.Blue,
    highlightColor: Color = Color(0xFF3F3F3F),
    animationDuration: Int = 800,
    enableAnimations: Boolean = true
) {
    require(input.isNotEmpty()) { "Input list cannot be empty" }
    require(input.all { it.value > 0 }) { "All values must be positive" }
    require(radius > innerRadius) { "Radius must be greater than innerRadius" }

    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var inputList by remember { mutableStateOf(input) }
    var isCenterTapped by remember { mutableStateOf(false) }

    // Animation states
    val rotationAngle by animateFloatAsState(
        targetValue = if (isCenterTapped && enableAnimations) 360f else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "rotation_animation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isCenterTapped && enableAnimations) 1.05f else 1f,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "scale_animation"
    )

    val segmentScale by animateFloatAsState(
        targetValue = if (inputList.any { it.isTapped } && enableAnimations) 1.1f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "segment_scale_animation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(input) {
                    detectTapGestures(
                        onTap = { offset ->
                            val tapAngleInDegrees = calculateTapAngle(circleCenter, offset)
                            val centerClicked = isCenterClick(circleCenter, offset, innerRadius, tapAngleInDegrees)

                            if (centerClicked) {
                                inputList = inputList.map { it.copy(isTapped = !isCenterTapped) }
                                isCenterTapped = !isCenterTapped
                            } else {
                                handleSegmentTap(input, inputList, tapAngleInDegrees) { updatedList ->
                                    inputList = updatedList
                                }
                            }
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            circleCenter = Offset(x = width / 2f, y = height / 2f)

            val totalValue = input.sumOf { it.value }.toFloat()
            val anglePerValue = 360f / totalValue
            var currentStartAngle = 0f

            // Apply overall rotation and scale animations
            rotate(rotationAngle) {
                scale(scale) {
                    inputList.forEach { pieChartInput ->
                        val scale = if (pieChartInput.isTapped) segmentScale else 1.0f
                        val angleToDraw = pieChartInput.value * anglePerValue

                        scale(scale) {
                            drawArc(
                                color = pieChartInput.color,
                                startAngle = currentStartAngle,
                                sweepAngle = angleToDraw,
                                useCenter = true,
                                size = Size(
                                    width = radius * 2f,
                                    height = radius * 2f
                                ),
                                topLeft = Offset(
                                    (width - radius * 2f) / 2f,
                                    (height - radius * 2f) / 2f
                                )
                            )
                        }
                        currentStartAngle += angleToDraw
                    }
                }
            }

            // Draw text and highlights
            currentStartAngle = 0f
            inputList.forEach { pieChartInput ->
                val angleToDraw = pieChartInput.value * anglePerValue
                drawSegmentText(
                    pieChartInput = pieChartInput,
                    totalValue = totalValue,
                    currentStartAngle = currentStartAngle,
                    angleToDraw = angleToDraw,
                    circleCenter = circleCenter,
                    radius = radius,
                    innerRadius = innerRadius,
                    textColor = textColor
                )

                if (pieChartInput.isTapped) {
                    drawSegmentHighlight(
                        pieChartInput = pieChartInput,
                        currentStartAngle = currentStartAngle,
                        angleToDraw = angleToDraw,
                        circleCenter = circleCenter,
                        radius = radius,
                        highlightColor = highlightColor,
                        textColor = textColor
                    )
                }
                currentStartAngle += angleToDraw
            }

            drawCenterCircle(circleCenter, innerRadius, transparentWidth)
        }

        centerText?.let { text ->
            androidx.compose.material3.Text(
                text = text,
                modifier = Modifier
                    .width(Dp(innerRadius / 1.5f))
                    .padding(25.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                color = centerTextColor
            )
        }
    }
}