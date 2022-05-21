package com.garan.tempo.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.garan.tempo.ui.navigation.Screen
import com.garan.tempo.ui.screens.settings.SettingsViewModel
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun SettingsScreen(
    uiState: UiState,
    screenStarted: Boolean = uiState.navHostController
        .getBackStackEntry(Screen.SETTINGS.route)
        .lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED),
    viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>()
) {
    val exerciseSettings by viewModel.exerciseSettings.collectAsStateLifecycleAware(initial = emptyList())
    val scalingListState = rememberScalingLazyListState()

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
        //item {
//            UnitsToggle(
//                units = units,
//                onCheckedChange = {
//                    // TODO
////                    scope.launch {
////                        settings.setUnits(
////                            when (it) {
////                                true -> Units.IMPERIAL
////                                else -> Units.METRIC
////                            }
////                        )
////                    }
//                }
//            )
        //}

        items(exerciseSettings) { item ->
            Chip(
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