package com.garan.tempo.ui.format

import androidx.compose.runtime.compositionLocalOf
import com.garan.tempo.R
import com.garan.tempo.data.metrics.TempoMetric
import kotlin.math.roundToInt

class DisplayUnitFormatter(
    // Default to distance unit of mile
    val distanceScaleFactor: Double = 1609.344,
    // Default to speed unit of mph
    val speedScaleFactor: Double = 2.23694,
    // Pace is by default in milliseconds/kilometer, convert to seconds per kilometer
    val paceScaleFactor: Double = 0.001,
    // Default to elevation unit of feet
    val elevationScaleFactor: Double = 3.28084,
    val labelMap: Map<TempoMetric, Int> = imperialLabelMap()
) {
    private val limitPace = 0.2

    fun formatValue(metricType: TempoMetric, value: Number) = when (metricType) {
        TempoMetric.DISTANCE,
        TempoMetric.FLAT_GROUND_DISTANCE,
        TempoMetric.INCLINE_DISTANCE,
        TempoMetric.DECLINE_DISTANCE -> {
            val distance = value.toDouble() / distanceScaleFactor
            if (distance >= 10) {
                "%.1f".format(distance)
            } else {
                "%.2f".format(distance)
            }
        }

        TempoMetric.SPEED,
        TempoMetric.AVG_SPEED,
        TempoMetric.MAX_SPEED -> "%.1f".format(value.toDouble() * speedScaleFactor)

        TempoMetric.CALORIES,
        TempoMetric.HEART_RATE_BPM,
        TempoMetric.AVG_HEART_RATE,
        TempoMetric.MAX_HEART_RATE -> roundedDoubleOrDashes(value.toDouble())

        TempoMetric.ACTIVE_DURATION,
        TempoMetric.DECLINE_DURATION,
        TempoMetric.INCLINE_DURATION,
        TempoMetric.RESTING_DURATION -> "%02d:%02d".format(
            value.toLong() / 60, value.toLong() % 60
        )

        // Pace is in milliseconds/kilometer
        TempoMetric.PACE,
        TempoMetric.AVG_PACE,
        TempoMetric.MAX_PACE -> {
            if (value.toDouble() < limitPace) {
                "--"
            } else {
                val pace = (paceScaleFactor * value.toDouble()).toLong()
                "%d:%02d".format(pace / 60, pace % 60)
            }
        }

        TempoMetric.CADENCE,
        TempoMetric.AVG_CADENCE,
        TempoMetric.MAX_CADENCE -> value.toLong().toString()

        TempoMetric.TOTAL_STEPS,
        TempoMetric.RUNNING_STEPS,
        TempoMetric.WALKING_STEPS,
        TempoMetric.FLOORS,
        TempoMetric.GOLF_SHOT_TOTAL,
        TempoMetric.REP_COUNT,
        TempoMetric.SWIMMING_LAPS,
        TempoMetric.SWIMMING_STROKES -> value.toLong().toString()

        TempoMetric.MAX_ELEVATION,
        TempoMetric.ELEVATION_GAIN,
        TempoMetric.ELEVATION_LOSS -> (value.toDouble() * elevationScaleFactor).roundToInt()
            .toString()
    }

    fun labelId(metricType: TempoMetric) =
        labelMap.getOrDefault(metricType, R.string.unknown_label)
}

private fun roundedDoubleOrDashes(value: Double) = if (!value.isNaN()) {
    value.roundToInt().toString()
} else {
    "--"
}

fun metricUnitFormatter() = DisplayUnitFormatter(
    distanceScaleFactor = 1000.0,
    speedScaleFactor = 3.6,
    // Conversion from ms/km to s/mile
    paceScaleFactor = 0.00062150403,
    elevationScaleFactor = 1.0,
    labelMap = metricLabelMap()
)

fun imperialUnitFormatter() = DisplayUnitFormatter()

val LocalDisplayUnitFormatter = compositionLocalOf { DisplayUnitFormatter() }

fun imperialLabelMap() = mapOf(
    TempoMetric.DISTANCE to R.string.imperial_distance_label,
    TempoMetric.FLAT_GROUND_DISTANCE to R.string.imperial_distance_label,
    TempoMetric.INCLINE_DISTANCE to R.string.imperial_distance_label,
    TempoMetric.DECLINE_DISTANCE to R.string.imperial_distance_label,
    TempoMetric.SPEED to R.string.imperial_speed_label,
    TempoMetric.AVG_SPEED to R.string.imperial_speed_label,
    TempoMetric.MAX_SPEED to R.string.imperial_speed_label,
    TempoMetric.CALORIES to R.string.imperial_calories_label,
    TempoMetric.HEART_RATE_BPM to R.string.imperial_hr_label,
    TempoMetric.AVG_HEART_RATE to R.string.imperial_hr_label,
    TempoMetric.MAX_HEART_RATE to R.string.imperial_hr_label,
    TempoMetric.PACE to R.string.imperial_pace_label,
    TempoMetric.AVG_PACE to R.string.imperial_pace_label,
    TempoMetric.MAX_PACE to R.string.imperial_pace_label,
    TempoMetric.CADENCE to R.string.imperial_cadence_label,
    TempoMetric.AVG_CADENCE to R.string.imperial_cadence_label,
    TempoMetric.MAX_CADENCE to R.string.imperial_cadence_label,
    TempoMetric.MAX_ELEVATION to R.string.imperial_elevation_label,
    TempoMetric.ELEVATION_GAIN to R.string.imperial_elevation_label,
    TempoMetric.ELEVATION_LOSS to R.string.imperial_elevation_label
)

fun metricLabelMap() = mapOf(
    TempoMetric.DISTANCE to R.string.metric_distance_label,
    TempoMetric.FLAT_GROUND_DISTANCE to R.string.metric_distance_label,
    TempoMetric.INCLINE_DISTANCE to R.string.metric_distance_label,
    TempoMetric.DECLINE_DISTANCE to R.string.metric_distance_label,
    TempoMetric.SPEED to R.string.metric_speed_label,
    TempoMetric.AVG_SPEED to R.string.metric_speed_label,
    TempoMetric.MAX_SPEED to R.string.metric_speed_label,
    TempoMetric.CALORIES to R.string.metric_calories_label,
    TempoMetric.HEART_RATE_BPM to R.string.metric_hr_label,
    TempoMetric.AVG_HEART_RATE to R.string.metric_hr_label,
    TempoMetric.MAX_HEART_RATE to R.string.metric_hr_label,
    TempoMetric.PACE to R.string.metric_pace_label,
    TempoMetric.AVG_PACE to R.string.metric_pace_label,
    TempoMetric.MAX_PACE to R.string.metric_pace_label,
    TempoMetric.CADENCE to R.string.metric_cadence_label,
    TempoMetric.AVG_CADENCE to R.string.metric_cadence_label,
    TempoMetric.MAX_CADENCE to R.string.metric_cadence_label,
    TempoMetric.MAX_ELEVATION to R.string.metric_elevation_label,
    TempoMetric.ELEVATION_GAIN to R.string.metric_elevation_label,
    TempoMetric.ELEVATION_LOSS to R.string.metric_elevation_label
)