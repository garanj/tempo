package com.garan.tempo.vibrations

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.health.services.client.data.ExerciseState
import androidx.lifecycle.LifecycleService

class TempoVibrationsManager(private val context: Context) {
    @Suppress("DEPRECATION")
    private val vibrator by lazy { context.getSystemService(LifecycleService.VIBRATOR_SERVICE) as Vibrator }

    private val pagerVibration = VibrationEffect.createOneShot(200, 250)

    private val stateVibrations = mapOf(
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
        ExerciseState.ENDING to VibrationEffect.createWaveform(
            longArrayOf(250L, 50L, 250L, 50L, 250L), intArrayOf(255, 0, 255, 0, 255), -1
        ),
        ExerciseState.AUTO_RESUMING to VibrationEffect.createWaveform(
            longArrayOf(100L, 50L, 50L, 50L, 50L, 50L),
            intArrayOf(255, 0, 255, 0, 255, 0), -1
        ),
        ExerciseState.AUTO_PAUSING to VibrationEffect.createWaveform(
            longArrayOf(500L), intArrayOf(255), -1
        )
    )

    fun vibrateByStateTransition(state: ExerciseState) {
        stateVibrations[state]?.let {
            vibrator.vibrate(it)
        }
    }

    fun vibrateForScroll() = vibrator.vibrate(pagerVibration)
}