package com.garan.tempo.settings.defaults

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import com.garan.tempo.settings.ExerciseSettings
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.ui.metrics.DisplayMetric

fun defaultWalkingSettings() = ExerciseSettings(
    "Walking",
    ExerciseType.WALKING,
    recordingMetrics = setOf(DataType.LOCATION),
    listOf(
        ScreenSettings(
            ScreenFormat.ONE_SLOT,
            listOf(
                DisplayMetric.ACTIVE_DURATION,
                DisplayMetric.AVG_SPEED
            )
        )
    )
)