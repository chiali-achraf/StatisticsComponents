package com.achraf.piechat

fun handleSegmentTap(
    originalInput: List<PieChartInput>,
    currentInput: List<PieChartInput>,
    tapAngle: Float,
    onUpdate: (List<PieChartInput>) -> Unit
) {
    val anglePerValue = 360f / originalInput.sumOf { it.value }
    var currAngle = 0f
    originalInput.forEach { pieChartInput ->
        currAngle += pieChartInput.value * anglePerValue
        if (tapAngle < currAngle) {
            val updatedList = currentInput.map {
                if (pieChartInput.description == it.description) {
                    it.copy(isTapped = !it.isTapped)
                } else {
                    it.copy(isTapped = false)
                }
            }
            onUpdate(updatedList)
            return
        }
    }
}