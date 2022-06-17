package com.garan.tempo.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import com.garan.tempo.UiState
import com.garan.tempo.settings.TempoSettings
import com.garan.tempo.settings.Units
import com.garan.tempo.ui.components.UnitsToggle
import com.garan.tempo.ui.navigation.Screen
import com.garan.tempo.ui.screens.startmenu.collectAsStateLifecycleAware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    uiState: UiState,
    screenStarted: Boolean = uiState.navHostController
        .getBackStackEntry(Screen.SETTINGS.route)
        .lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED),
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val exerciseSettings by viewModel.exerciseSettings.collectAsStateLifecycleAware(initial = emptyList())
    val tempoSettings by viewModel.tempoSettingsManager.tempoSettings.collectAsState(initial = TempoSettings(units = Units.METRIC))
    val scalingListState = rememberScalingLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(screenStarted) {
        if (screenStarted) {
            uiState.isShowTime.value = false
            uiState.isShowVignette.value = true
        }
    }
    ScalingLazyColumn(
        anchorType = ScalingLazyListAnchorType.ItemStart,
        state = scalingListState
    ) {
        item {
            UnitsToggle(
                units = tempoSettings.units,
                onCheckedChange = {
                    scope.launch(Dispatchers.IO) {
                        val newValue = when (it) {
                            true -> Units.IMPERIAL
                            else -> Units.METRIC
                        }
                        viewModel.tempoSettingsManager.setUnits(newValue)
                    }
                }
            )
        }

        items(exerciseSettings) { item ->
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                label = { Text(item.exerciseSettings.name) },
                onClick = {
                    uiState.navHostController.navigate(
                        Screen.WORKOUT_SETTINGS.route + "/${item.exerciseSettings.exerciseSettingsId}"
                    )
                }
            )
        }
    }
}