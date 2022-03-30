package com.garan.tempo.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.Screen
import com.garan.tempo.UiState
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.settings.Units
import com.garan.tempo.settings.defaults.defaultExerciseSettingsList
import com.garan.tempo.ui.components.UnitsToggle
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun SettingsScreen(
    uiState: UiState,
    settings: TempoSettingsManager,
    screenStarted: Boolean = uiState.navHostController
        .getBackStackEntry(Screen.SETTINGS.route)
        .lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
) {
    val units by settings.units.collectAsState(Units.METRIC)
    val exercises by settings.exercises.collectAsState(defaultExerciseSettingsList())
    val scope = rememberCoroutineScope()

    LaunchedEffect(screenStarted) {
        if (screenStarted) {
            uiState.isShowTime.value = false
            uiState.isShowVignette.value = true
        }
    }
    ScalingLazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 10.dp,
            end = 20.dp,
            bottom = 30.dp
        )
    ) {
        item {
            UnitsToggle(
                units = units,
                onCheckedChange = {
                    scope.launch {
                        settings.setUnits(
                            when (it) {
                                true -> Units.IMPERIAL
                                else -> Units.METRIC
                            }
                        )
                    }
                }
            )
        }

        items(exercises) { item ->
            Chip(
                label = { Text(item.name) },
                onClick = {
                    uiState.navHostController.navigate(
                        Screen.WORKOUT_SETTINGS.route + "/${item.id}"
                    )
                }
            )
        }
    }
}