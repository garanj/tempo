package com.garan.tempo.ui.screens.startmenu

import androidx.lifecycle.ViewModel
import com.garan.tempo.HealthServicesManager
import com.garan.tempo.settings.TempoSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartMenuViewModel @Inject constructor(
    val tempoSettingsManager: TempoSettingsManager,
    val healthServicesManager: HealthServicesManager
) : ViewModel() {
    val exerciseSettings = tempoSettingsManager.getAllExerciseSettings()
}

