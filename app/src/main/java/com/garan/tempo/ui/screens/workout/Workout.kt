package com.garan.tempo.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import com.garan.tempo.data.AvailabilityHolder
import com.garan.tempo.ui.components.ambient.AmbientState

/**
 * Composable functions for use when connected to the fan, either when in HR-guided or non-HR mode.
 */
val LocalDataAvailability = compositionLocalOf { AvailabilityHolder() }

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
        val metrics by serviceState.metrics
        val checkpoint by serviceState.checkpoint
        val settings by serviceState.settings
        val exerciseId by serviceState.exerciseId
        val availability by serviceState.availability
        CompositionLocalProvider(LocalDataAvailability provides availability) {
            ActiveScreen(
                metricsUpdate = metrics,
                checkpoint = checkpoint,
                exerciseState = exerciseState,
                screenList = settings?.screenSettings ?: listOf(),
                onFinishTap = onFinishTap,
                onPauseResumeTap = onPauseResumeTap,
                onFinishStateChange = {
                    exerciseId?.let {
                        onFinishStateChange(it.toString())
                    }
                },
                onActiveScreenChange = onActiveScreenChange,
                ambientState = ambientState
            )
        }
    }
}