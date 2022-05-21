package com.garan.tempo.ui.model

import androidx.health.services.client.data.ExerciseCapabilities
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garan.tempo.HealthServicesManager
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.ui.metrics.DisplayMetric
import com.garan.tempo.ui.metrics.getSupportedDisplayMetrics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricPickerViewModel @Inject constructor(
    val tempoSettingsManager: TempoSettingsManager,
    val healthServicesManager: HealthServicesManager,
    val capabilities: Deferred<ExerciseCapabilities>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val settingsId = savedStateHandle.get<Int>("settingsId")!!

    val displayMetrics: Flow<MetricPickerUiState> =
        tempoSettingsManager.getExerciseSettings(settingsId)
            .transform { settings ->
                val exerciseType = settings.exerciseSettings.exerciseType
                val supportedCapabilities = capabilities.getCompleted()
                    .getExerciseTypeCapabilities(exerciseType)
                val dataTypes = supportedCapabilities.supportedDataTypes
                val displayMetrics = getSupportedDisplayMetrics(dataTypes)
                emit(
                    MetricPickerUiState(
                        settingsId = settingsId,
                        displayMetrics = displayMetrics
                    )
                )
            }

    fun setMetric(settingsId: Int, screen: Int, slot: Int, metric: DisplayMetric) {
        viewModelScope.launch(Dispatchers.IO) {
            tempoSettingsManager.setMetric(settingsId, screen, slot, metric)
        }
    }
}

data class MetricPickerUiState(
    val settingsId: Int = 0,
    val displayMetrics: Set<DisplayMetric> = setOf()
)
