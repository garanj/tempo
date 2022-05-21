package com.garan.tempo.settings

import androidx.health.services.client.data.ExerciseCapabilities
import com.garan.tempo.ui.metrics.DisplayMetric
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class TempoSettingsManager @Inject constructor(
    private val exerciseSettingsDao: ExerciseSettingsDao,
    private val capabilities: Deferred<ExerciseCapabilities>,
    private val tempoSettingsDao: TempoSettingsDao
) {
    val initialized = MutableStateFlow(false)

    init {
        capabilities.invokeOnCompletion {
            initialized.value = true
        }
    }

    fun getAllExerciseSettings() = exerciseSettingsDao.getExerciseSettingsWithScreenSettings()
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

    suspend fun setMetric(settingsId: Int, screen: Int, slot: Int, metric: DisplayMetric) {
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

    suspend fun setAutoPause(settingsId: Int, enabled: Boolean) {
        val settings = exerciseSettingsDao.getExerciseSettings(settingsId)
        val newSettings = settings.copy(useAutoPause = enabled)
        exerciseSettingsDao.updateExerciseSettings(newSettings)
    }
}

enum class Units {
    METRIC,
    IMPERIAL
}