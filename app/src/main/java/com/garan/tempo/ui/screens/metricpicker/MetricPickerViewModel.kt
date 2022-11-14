package com.garan.tempo.ui.screens.metricpicker

import androidx.health.services.client.data.ExerciseCapabilities
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.garan.tempo.HealthServicesManager
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.settings.TempoSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@HiltViewModel
class MetricPickerViewModel @Inject constructor(
    val tempoSettingsManager: TempoSettingsManager,
    val healthServicesManager: HealthServicesManager,
    private val capabilities: Deferred<ExerciseCapabilities>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val settingsId = savedStateHandle.get<Int>("settingsId")!!

    @OptIn(ExperimentalCoroutinesApi::class)
    val displayMetrics: Flow<MetricPickerUiState> =
        tempoSettingsManager.getExerciseSettings(settingsId)
            .transform { settings ->
                val exerciseType = settings.exerciseSettings.exerciseType
                val supportedCapabilities = capabilities.getCompleted()
                    .getExerciseTypeCapabilities(exerciseType)
                val dataTypes = supportedCapabilities.supportedDataTypes
                val displayMetrics = TempoMetric.getSupportedTempoMetrics(dataTypes)
                emit(
                    MetricPickerUiState(
                        settingsId = settingsId,
                        tempoMetrics = displayMetrics
                    )
                )
            }

    suspend fun setMetric(settingsId: Int, screen: Int, slot: Int, metric: TempoMetric) {
        tempoSettingsManager.setMetric(settingsId, screen, slot, metric)
    }
}

data class MetricPickerUiState(
    val settingsId: Int = 0,
    val tempoMetrics: Set<TempoMetric> = setOf()
)
