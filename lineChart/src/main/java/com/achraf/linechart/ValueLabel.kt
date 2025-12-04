package com.achraf.linechart

import java.text.NumberFormat
import java.util.Locale


/**
 * Configuration for value label formatting
 */
data class ValueLabelConfig(
    val locale: Locale = Locale.getDefault(),
    val decimalFormatting: (Float) -> Int = { value ->
        when {
            value > 1000 -> 0
            value in 2f..999f -> 2
            else -> 3
        }
    }
)

/**
 * Internal data class for value labels
 */
internal data class ValueLabel(
    val value: Float,
    val unit: String,
    val config: ValueLabelConfig = ValueLabelConfig()
) {
    fun formatted(): String {
        val formatter = NumberFormat.getNumberInstance(config.locale).apply {
            val fractionDigits = config.decimalFormatting(value)
            maximumFractionDigits = fractionDigits
            minimumFractionDigits = 0
        }
        return "${formatter.format(value)}$unit"
    }
}