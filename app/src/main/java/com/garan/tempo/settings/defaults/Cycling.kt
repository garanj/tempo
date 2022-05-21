package com.garan.tempo.settings.defaults

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import com.garan.tempo.settings.ExerciseSettings
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.ui.metrics.DisplayMetric

fun defaultCyclingExerciseSettings() = ExerciseSettings(
    name = "Cycling",
    exerciseType = ExerciseType.BIKING,
    recordingMetrics = setOf(DataType.LOCATION),
    useAutoPause = true
)

fun defaultCyclingScreenSettings() = listOf(
    ScreenSettings(
        screenFormat = ScreenFormat.ONE_SLOT,
        metrics = listOf(
            DisplayMetric.ACTIVE_DURATION,
            DisplayMetric.AVG_SPEED
        )
    )
)