package com.garan.tempo.settings.defaults

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import com.garan.tempo.settings.ExerciseSettings
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.ui.metrics.DisplayMetric

fun defaultRunningExerciseSettings() = ExerciseSettings(
    name ="Running",
    exerciseType = ExerciseType.RUNNING,
    recordingMetrics = setOf(
        DataType.LOCATION,
        DataType.HEART_RATE_BPM,
        DataType.STEPS_PER_MINUTE
    ),
    useAutoPause = true
)

fun defaultRunningScreenSettings() = listOf(
        // Screen 1: Averages
        ScreenSettings(
            screenFormat = ScreenFormat.ONE_PLUS_FOUR_SLOT,
            metrics = listOf(
                DisplayMetric.AVG_PACE,
                DisplayMetric.AVG_HEART_RATE,
                DisplayMetric.ACTIVE_DURATION,
                DisplayMetric.AVG_CADENCE,
                DisplayMetric.DISTANCE
            )
        ),
        // Screen 2: Current
        ScreenSettings(
            screenFormat = ScreenFormat.SIX_SLOT,
            metrics = listOf(
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
            screenFormat = ScreenFormat.ONE_PLUS_TWO_SLOT,
            metrics = listOf(
                DisplayMetric.PACE,
                DisplayMetric.ACTIVE_DURATION,
                DisplayMetric.DISTANCE
            )
        ),
        // Screen 4: Basic
        ScreenSettings(
            screenFormat = ScreenFormat.TWO_SLOT,
            metrics = listOf(
                DisplayMetric.PACE,
                DisplayMetric.ACTIVE_DURATION
            )
        ),
        // Screen 5: Single
        ScreenSettings(
            screenFormat = ScreenFormat.ONE_SLOT,
            metrics = listOf(
                DisplayMetric.ACTIVE_DURATION
            )
        )
    )
