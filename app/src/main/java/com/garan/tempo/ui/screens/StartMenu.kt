package com.garan.tempo.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.health.services.client.data.ExerciseState
import androidx.lifecycle.Lifecycle
import androidx.wear.compose.material.ScalingLazyColumn
import com.garan.tempo.Screen
import com.garan.tempo.TempoService
import com.garan.tempo.UiState
import com.garan.tempo.ui.components.MenuEntry
import com.garan.tempo.ui.components.MenuItem

/**
 * Composable functions used on the Connect screen, for initiating a connection to the fan.
 */
@Composable
fun StartMenuScreen(
    uiState: UiState,
    service : TempoService,
    startMenuItems: List<MenuItem>,
    screenStarted: Boolean = uiState.navHostController
        .getBackStackEntry(Screen.START_MENU.route)
        .lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
) {
    val exerciseState by service.exerciseState

    LaunchedEffect(screenStarted) {
        if (screenStarted) {
            uiState.isShowTime.value = true
        }
    }
    LaunchedEffect(screenStarted, exerciseState) {
        if (screenStarted && exerciseState == ExerciseState.PREPARING) {
            uiState.navHostController.navigate(Screen.PRE_WORKOUT.route)
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
        items(startMenuItems.size) {
            MenuEntry(menuItem = startMenuItems[it])
        }
    }
}