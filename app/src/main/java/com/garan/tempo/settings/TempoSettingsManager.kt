package com.garan.tempo.settings

import androidx.health.services.client.data.ExerciseCapabilities
import com.garan.tempo.ui.metrics.TempoMetric
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class TempoSettingsManager @Inject constructor(
    private val exerciseSettingsDao: ExerciseSettingsDao,
    private val tempoSettingsDao: TempoSettingsDao,
    private val capabilities: Deferred<ExerciseCapabilities>
) {
    private val initialized = MutableStateFlow(false)

    val tempoSettings = tempoSettingsDao.getTempoSettingsFlow()

    init {
        capabilities.invokeOnCompletion {
            initialized.value = true
        }
    }

    fun getAllExerciseSettings(): Flow<List<ExerciseSettingsWithScreens>> =
        exerciseSettingsDao.getExerciseSettingsWithScreenSettings()
            .combine(initialized) { settingsList, initialized ->
                if (initialized) {
                    settingsList.filter { settings ->
                        capabilities.getCompleted().supportedExerciseTypes
                            .contains(settings.exerciseSettings.exerciseType)
                    }
                } else {
                    listOf()
                }
            }

    fun getExerciseSettings(settingsId: Int) =
        exerciseSettingsDao.getExerciseSettingsWithScreenSettings(settingsId)
            .combine(initialized) { settings, initialized ->
                if (initialized) {
                    settings.exerciseSettings.supportsAutoPause = capabilities.getCompleted()
                        .getExerciseTypeCapabilities(settings.exerciseSettings.exerciseType).supportsAutoPauseAndResume
                }
                settings
            }

    suspend fun setMetric(settingsId: Int, screen: Int, slot: Int, metric: TempoMetric) {
        val screenSettings = exerciseSettingsDao.getScreen(settingsId, screen)
        val metrics = screenSettings.metrics.mapIndexed { index, displayMetric ->
            if (index == slot) {
                metric
            } else {
                displayMetric
            }
        }
        val newScreen = screenSettings.copy(metrics = metrics)
        exerciseSettingsDao.updateScreenSettings(newScreen)
    }

    suspend fun setScreenFormat(settingsId: Int, screen: Int, screenFormat: ScreenFormat) {
        val screenSettings = exerciseSettingsDao.getScreen(settingsId, screen)
        val newScreen = screenSettings.copy(screenFormat = screenFormat)
        exerciseSettingsDao.updateScreenSettings(newScreen)
    }

    suspend fun setAutoPause(settingsId: Int, enabled: Boolean) {
        val settings = exerciseSettingsDao.getExerciseSettings(settingsId)
        val newSettings = settings.copy(useAutoPause = enabled)
        exerciseSettingsDao.updateExerciseSettings(newSettings)
    }

    fun setUnits(units: Units) {
        val settings = tempoSettingsDao.getTempoSettings()
        val newSettings = settings.copy(units = units)
        tempoSettingsDao.update(newSettings)
    }
}

