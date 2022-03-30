package com.garan.tempo.ui.format

import androidx.compose.runtime.compositionLocalOf
import androidx.health.services.client.data.Value
import com.garan.tempo.R
import com.garan.tempo.ui.metrics.DisplayMetric
import kotlin.math.roundToInt

class DisplayUnitFormatter(
    val distanceScaleFactor: Double = 1609.344,
    val speedScaleFactor: Double = 2.23694,
    val labelMap: Map<DisplayMetric, Int> = imperialLabelMap(),
    val plop: String = "imp"
) {
    private val limitPace = 0.2

    fun formatValue(metricType: DisplayMetric, value: Value) = when(metricType) {
        DisplayMetric.DISTANCE -> {
            val distance = value.asDouble() / distanceScaleFactor
            if (distance >= 10) {
                "%.1f".format(distance)
            } else {
                "%.2f".format(distance)
            }
        }
        DisplayMetric.SPEED,
        DisplayMetric.AVG_SPEED -> "%.1f".format(value.asDouble() * speedScaleFactor)
        DisplayMetric.CALORIES -> value.asDouble().roundToInt().toString()
        DisplayMetric.HEART_RATE_BPM,
        DisplayMetric.AVG_HEART_RATE -> value.asDouble().roundToInt().toString()
        DisplayMetric.ACTIVE_DURATION -> "%02d:%02d".format(
            value.asLong() / 60, value.asLong() % 60
        )
        DisplayMetric.PACE,
        DisplayMetric.AVG_PACE -> {
            if (value.asDouble() < limitPace) {
                "--"
            } else {
                val pace = (distanceScaleFactor / value.asDouble()).toLong()
                "%02d:%02d".format(pace / 60, pace % 60)
            }
        }
        DisplayMetric.CADENCE,
        DisplayMetric.AVG_CADENCE -> value.asLong().toString()
    }

    fun labelId(metricType: DisplayMetric) = labelMap.getOrDefault(metricType, R.string.unknown_label)
}

fun metricUnitFormatter() = DisplayUnitFormatter(
    distanceScaleFactor = 1000.0,
    speedScaleFactor = 3.6,
    labelMap = metricLabelMap(),
    plop = "met"
)

fun imperialUnitFormatter() = DisplayUnitFormatter()

val LocalDisplayUnitFormatter = compositionLocalOf { DisplayUnitFormatter() }

fun imperialLabelMap() = mapOf<DisplayMetric, Int>(
    DisplayMetric.DISTANCE to R.string.imperial_distance_label,
    DisplayMetric.SPEED to R.string.imperial_speed_label,
    DisplayMetric.AVG_SPEED to R.string.imperial_speed_label,
    DisplayMetric.CALORIES to R.string.imperial_calories_label,
    DisplayMetric.HEART_RATE_BPM to R.string.imperial_hr_label,
    DisplayMetric.AVG_HEART_RATE to R.string.imperial_hr_label,
    DisplayMetric.PACE to R.string.imperial_pace_label,
    DisplayMetric.AVG_PACE to R.string.imperial_pace_label,
    DisplayMetric.CADENCE to R.string.imperial_cadence_label,
    DisplayMetric.AVG_CADENCE to R.string.imperial_cadence_label
)

fun metricLabelMap() = mapOf<DisplayMetric, Int>(
    DisplayMetric.DISTANCE to R.string.metric_distance_label,
    DisplayMetric.SPEED to R.string.metric_speed_label,
    DisplayMetric.AVG_SPEED to R.string.metric_speed_label,
    DisplayMetric.CALORIES to R.string.metric_calories_label,
    DisplayMetric.HEART_RATE_BPM to R.string.metric_hr_label,
    DisplayMetric.AVG_HEART_RATE to R.string.metric_hr_label,
    DisplayMetric.PACE to R.string.metric_pace_label,
    DisplayMetric.AVG_PACE to R.string.metric_pace_label,
    DisplayMetric.CADENCE to R.string.metric_cadence_label,
    DisplayMetric.AVG_CADENCE to R.string.metric_cadence_label
)