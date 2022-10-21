package com.garan.tempo.settings.defaults

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.settings.ExerciseSettings
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.ScreenSettings

fun defaultRunningExerciseSettings() = ExerciseSettings(
    name = "Running",
    exerciseType = ExerciseType.RUNNING,
    recordingMetrics = setOf(
        DataType.LOCATION,
        DataType.HEART_RATE_BPM,
        DataType.STEPS_PER_MINUTE
    ),
    endSummaryMetrics = listOf(
        TempoMetric.ACTIVE_DURATION,
        TempoMetric.DISTANCE,
        TempoMetric.AVG_PACE,
        TempoMetric.AVG_HEART_RATE,
        TempoMetric.CALORIES
    ),
    useAutoPause = true
)

fun defaultRunningScreenSettings() = listOf(
    // Screen 1: Averages
    ScreenSettings(
        screenFormat = ScreenFormat.ONE_PLUS_FOUR_SLOT,
        metrics = listOf(
            TempoMetric.AVG_PACE,
            TempoMetric.AVG_HEART_RATE,
            TempoMetric.ACTIVE_DURATION,
            TempoMetric.AVG_CADENCE,
            TempoMetric.DISTANCE,
            TempoMetric.CALORIES
        )
    ),
    // Screen 2: Current
    ScreenSettings(
        screenFormat = ScreenFormat.SIX_SLOT,
        metrics = listOf(
            TempoMetric.ACTIVE_DURATION,
            TempoMetric.PACE,
            TempoMetric.HEART_RATE_BPM,
            TempoMetric.DISTANCE,
            TempoMetric.CADENCE,
            TempoMetric.CALORIES
        )
    ),
    // Screen 3: Other
    ScreenSettings(
        screenFormat = ScreenFormat.ONE_PLUS_TWO_SLOT,
        metrics = listOf(
            TempoMetric.PACE,
            TempoMetric.ACTIVE_DURATION,
            TempoMetric.DISTANCE,
            TempoMetric.HEART_RATE_BPM,
            TempoMetric.CADENCE,
            TempoMetric.CALORIES
        )
    )
)
