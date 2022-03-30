package com.garan.tempo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.health.services.client.data.ExerciseState
import androidx.lifecycle.Lifecycle
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.DisplayUpdateMap
import com.garan.tempo.Screen
import com.garan.tempo.UiState
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.ui.components.EndRing
import com.garan.tempo.ui.components.SixSlotMetricDisplay
import com.garan.tempo.ui.components.display.OnePlusFourSlotDisplay
import com.garan.tempo.ui.components.display.OnePlusTwoSlotDisplay
import com.garan.tempo.ui.components.display.OneSlotDisplay
import com.garan.tempo.ui.components.display.TwoSlotDisplay
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.VerticalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

/**
 * Composable functions for use when connected to the fan, either when in HR-guided or non-HR mode.
 */

@ExperimentalPagerApi
@Composable
fun WorkoutScreen(
    screenList: List<ScreenSettings>,
    metricsUpdate: DisplayUpdateMap,
    exerciseState: ExerciseState,
    uiState: UiState,
    screenStarted: Boolean = uiState.navHostController
        .getBackStackEntry(Screen.WORKOUT.route)
        .lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED),
    onPauseTap: () -> Unit,
    onFinishTap: () -> Unit
) {
    // TODO handle autopause color and tap
    LaunchedEffect(screenStarted, exerciseState) {
        if (screenStarted) {
            if (exerciseState.isEnded) {
                uiState.navHostController.popBackStack(Screen.WORKOUT.route, true)
                uiState.navHostController.navigate(Screen.POST_WORKOUT.route)
            }
        }
    }

    var showTimer by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(1)
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        VerticalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colors.onSurface,
            inactiveColor = MaterialTheme.colors.secondary
        )
    }

    VerticalPager(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        showTimer = true
                        tryAwaitRelease()
                        showTimer = false
                    },
                    onDoubleTap = { onPauseTap() },
                    onLongPress = { /* Called on Long Press */ },
                    onTap = { /* Called on Tap */ }
                )
            },
        count = screenList.size,
        state = pagerState
    ) { page ->
        when(screenList[page].screenFormat) {
            ScreenFormat.ONE_PLUS_FOUR_SLOT -> OnePlusFourSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = exerciseState
            )
            ScreenFormat.SIX_SLOT -> SixSlotMetricDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = exerciseState
            )
            ScreenFormat.ONE_PLUS_TWO_SLOT -> OnePlusTwoSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = exerciseState
            )
            ScreenFormat.TWO_SLOT -> TwoSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = exerciseState
            )
            else -> OneSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = exerciseState
            )
        }
    }
    if (showTimer) {
        EndRing(onFinishTap = onFinishTap)
    }
}


