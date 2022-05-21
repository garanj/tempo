package com.garan.tempo.settings.defaults

fun defaultExerciseSettingsList() = listOf(
        defaultCyclingExerciseSettings() to defaultCyclingScreenSettings(),
        defaultRunningExerciseSettings() to defaultRunningScreenSettings(),
        defaultWalkingExerciseSettings() to defaultWalkingScreenSettings()
    )