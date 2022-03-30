package com.garan.tempo.settings.defaults

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import com.garan.tempo.settings.ExerciseSettings
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.ui.metrics.DisplayMetric

fun defaultRunningSettings() = ExerciseSettings(
    "Running",
    ExerciseType.RUNNING,
    recordingMetrics = setOf(
        DataType.LOCATION,
        DataType.HEART_RATE_BPM,
        DataType.STEPS_PER_MINUTE
    ),
    screens = listOf(
        // Screen 1: Averages
        ScreenSettings(
            ScreenFormat.ONE_PLUS_FOUR_SLOT,
            listOf(
                DisplayMetric.AVG_PACE,
                DisplayMetric.AVG_HEART_RATE,
                DisplayMetric.ACTIVE_DURATION,
                DisplayMetric.AVG_CADENCE,
                DisplayMetric.DISTANCE
            )
        ),
        // Screen 2: Current
        ScreenSettings(
            ScreenFormat.SIX_SLOT,
            listOf(
                DisplayMetric.ACTIVE_DURATION,
                DisplayMetric.PACE,
                DisplayMetric.HEART_RATE_BPM,
                DisplayMetric.DISTANCE,
                DisplayMetric.CADENCE,
                DisplayMetric.CALORIES
            )
        ),
        // Screen 3: Current
        ScreenSettings(
            ScreenFormat.ONE_PLUS_TWO_SLOT,
            listOf(
                DisplayMetric.PACE,
                DisplayMetric.ACTIVE_DURATION,
                DisplayMetric.DISTANCE
            )
        ),
        // Screen 4: Basic
        ScreenSettings(
            ScreenFormat.TWO_SLOT,
            listOf(
                DisplayMetric.PACE,
                DisplayMetric.ACTIVE_DURATION
            )
        ),
        // Screen 5: Single
        ScreenSettings(
            ScreenFormat.ONE_SLOT,
            listOf(
                DisplayMetric.ACTIVE_DURATION
            )
        )
    )
)