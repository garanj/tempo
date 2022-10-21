package com.garan.tempo.settings.defaults

// TODO - handle all exercise types
fun defaultExerciseSettingsList() = listOf(
    defaultCyclingExerciseSettings() to defaultCyclingScreenSettings(),
    defaultRunningExerciseSettings() to defaultRunningScreenSettings(),
    defaultWalkingExerciseSettings() to defaultWalkingScreenSettings()
)