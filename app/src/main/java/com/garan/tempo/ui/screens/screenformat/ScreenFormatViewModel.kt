package com.garan.tempo.ui.screens.screenformat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.TempoSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScreenFormatViewModel @Inject constructor(
    val tempoSettingsManager: TempoSettingsManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val settingsId = savedStateHandle.get<Int>("settingsId")!!
    private val screen = savedStateHandle.get<Int>("screen")!!

    fun setScreenFormat(screenFormat: ScreenFormat) {
        viewModelScope.launch {
            tempoSettingsManager.setScreenFormat(
                settingsId, screen, screenFormat
            )
        }
    }
}