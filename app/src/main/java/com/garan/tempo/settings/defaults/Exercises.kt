package com.garan.tempo.settings.defaults

import androidx.health.services.client.data.ExerciseType

fun defaultExerciseSettingsList() = listOf(
        defaultRunningSettings(),
        defaultWalkingSettings(),
        defaultCyclingSettings()
    )