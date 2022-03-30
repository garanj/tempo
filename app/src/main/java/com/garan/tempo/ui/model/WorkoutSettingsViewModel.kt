package com.garan.tempo.ui.model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.garan.tempo.HealthServicesManager
import com.garan.tempo.Screen
import com.garan.tempo.TAG
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.settings.TempoSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkoutSettingsViewModel @Inject constructor(
    val tempoSettingsManager: TempoSettingsManager,
    val healthServicesManager: HealthServicesManager,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    var uiState = mutableStateOf<WorkoutSettingsUiState>(WorkoutSettingsUiState())
    private set

    init {
        viewModelScope.launch {
            val settingsId = savedStateHandle.get<String>("settingsId")!!
            val workoutSettings = tempoSettingsManager.exercises.first().first {
                it.id == UUID.fromString(settingsId)
            }

            val capabilities = healthServicesManager.getCapabilities(workoutSettings.exerciseType)

            uiState.value = WorkoutSettingsUiState(
                name = workoutSettings.name,
                exerciseType = workoutSettings.exerciseType,
                supportsAutoPause = capabilities.supportsAutoPauseAndResume,
                screens = workoutSettings.screens
            )
        }
    }
}

data class WorkoutSettingsUiState(
    val name: String = "",
    val exerciseType: ExerciseType = ExerciseType.UNKNOWN,
    val supportsAutoPause: Boolean = false,
    val screens: List<ScreenSettings> = listOf()
)
