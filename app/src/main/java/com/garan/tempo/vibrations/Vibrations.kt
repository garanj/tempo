package com.garan.tempo.vibrations

import android.os.VibrationEffect
import androidx.health.services.client.data.ExerciseState

val stateVibrations = mapOf(
    ExerciseState.USER_STARTING to VibrationEffect.createWaveform(
        longArrayOf(100L, 50L), intArrayOf(255, 0), -1
    ),
    ExerciseState.USER_RESUMING to VibrationEffect.createWaveform(
        longArrayOf(100L, 50L, 50L, 50L, 50L, 50L),
        intArrayOf(255, 0, 255, 0, 255, 0), -1
    ),
    ExerciseState.USER_PAUSING to VibrationEffect.createWaveform(
        longArrayOf(500L), intArrayOf(255), -1
    ),
    ExerciseState.USER_ENDING to VibrationEffect.createWaveform(
        longArrayOf(250L, 50L, 250L, 50L, 250L), intArrayOf(255, 0, 255, 0, 255), -1
    ),
    ExerciseState.AUTO_RESUMING to VibrationEffect.createWaveform(
        longArrayOf(100L, 50L, 50L, 50L, 50L, 50L),
        intArrayOf(255, 0, 255, 0, 255, 0), -1
    ),
    ExerciseState.AUTO_PAUSING to VibrationEffect.createWaveform(
        longArrayOf(500L), intArrayOf(255), -1
    ),
    ExerciseState.AUTO_ENDING to VibrationEffect.createWaveform(
        longArrayOf(250L, 50L, 250L, 50L, 250L), intArrayOf(255, 0, 255, 0, 255), -1
    ),
    ExerciseState.AUTO_ENDING_PERMISSION_LOST to VibrationEffect.createWaveform(
        longArrayOf(3000L), intArrayOf(255), -1
    ),
)