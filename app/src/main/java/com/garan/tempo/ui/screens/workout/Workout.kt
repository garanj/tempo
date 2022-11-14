package com.garan.tempo.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.garan.tempo.ui.components.ambient.AmbientState

/**
 * Composable functions for use when connected to the fan, either when in HR-guided or non-HR mode.
 */

@Composable
fun WorkoutScreen(
    serviceState: ServiceState,
    onFinishStateChange: (String) -> Unit,
    onFinishTap: () -> Unit,
    onPauseResumeTap: () -> Unit,
    onActiveScreenChange: () -> Unit,
    ambientState: AmbientState
) {
    if (serviceState is ServiceState.Connected) {
        val exerciseState by serviceState.exerciseState
        val exercise by serviceState.exercise
        val checkpoint by serviceState.checkpoint
        val settings by serviceState.settings
        ActiveScreen(
            metricsUpdate = exercise.metricsMap,
            checkpoint = checkpoint,
            exerciseState = exerciseState,
            screenList = settings?.screenSettings ?: listOf(),
            onFinishTap = onFinishTap,
            onPauseResumeTap = onPauseResumeTap,
            onFinishStateChange = {
                onFinishStateChange(exercise.id.toString())
            },
            onActiveScreenChange = onActiveScreenChange,
            ambientState = ambientState,
            availabilityHolder = exercise.availability
        )
    }
}