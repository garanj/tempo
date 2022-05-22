package com.garan.tempo.ui.screens.workoutsettings

import androidx.health.services.client.data.ExerciseType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garan.tempo.HealthServicesManager
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.settings.TempoSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutSettingsViewModel @Inject constructor(
    val tempoSettingsManager: TempoSettingsManager,
    val healthServicesManager: HealthServicesManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val settingsId = savedStateHandle.get<Int>("settingsId")!!

    val exerciseSettings = tempoSettingsManager.getExerciseSettings(settingsId)
        .transform {
            emit(
                WorkoutSettingsUiState(
                    name = it.exerciseSettings.name,
                    exerciseType = it.exerciseSettings.exerciseType,
                    supportsAutoPause = it.exerciseSettings.supportsAutoPause,
                    useAutoPause = it.exerciseSettings.useAutoPause,
                    screens = it.screenSettings
                )
            )
        }

    fun setAutoPause(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            tempoSettingsManager.setAutoPause(settingsId, enabled)
        }
    }
}

data class WorkoutSettingsUiState(
    val name: String = "",
    val exerciseType: ExerciseType = ExerciseType.UNKNOWN,
    val supportsAutoPause: Boolean = false,
    val useAutoPause: Boolean = false,
    val screens: List<ScreenSettings> = listOf()
)
