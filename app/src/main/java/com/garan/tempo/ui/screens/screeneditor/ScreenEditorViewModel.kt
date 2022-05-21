package com.garan.tempo.ui.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.garan.tempo.settings.TempoSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScreenEditorViewModel @Inject constructor(
    val tempoSettingsManager: TempoSettingsManager,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val settingsId = savedStateHandle.get<Int>("settingsId")!!

    val exerciseSettings = tempoSettingsManager.getExerciseSettings(settingsId)
}
