package com.garan.tempo.ui.screens.workout

import android.util.Log
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.health.services.client.data.ExerciseState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.DisplayUpdateMap
import com.garan.tempo.TAG
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.ui.components.EndRing
import com.garan.tempo.ui.components.display.OnePlusFourSlotDisplay
import com.garan.tempo.ui.components.display.OnePlusTwoSlotDisplay
import com.garan.tempo.ui.components.display.OneSlotDisplay
import com.garan.tempo.ui.components.display.SixSlotMetricDisplay
import com.garan.tempo.ui.components.display.TwoSlotDisplay
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.VerticalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

/**
 * Composable functions for use when connected to the fan, either when in HR-guided or non-HR mode.
 */

@Composable
fun WorkoutScreen(
    onFinishNavigate: () -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val serviceState by viewModel.serviceState

    if (serviceState is ServiceState.Connected) {
        val service = serviceState as ServiceState.Connected
        val exerciseState by service.exerciseState
        val metrics = service.metrics
        val settings = service.settings!!
        ActiveScreen(
            metricsUpdate = metrics,
            exerciseState = exerciseState,
            screenList = settings.screenSettings,
            onFinishTap = {
                viewModel.endExercise()
            },
            onPauseTap = {
                viewModel.pauseResumeExercise()
            },
            onFinishNavigate = onFinishNavigate
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ActiveScreen(
    metricsUpdate: DisplayUpdateMap,
    screenList: List<ScreenSettings>,
    exerciseState: ExerciseState,
    onPauseTap: () -> Unit,
    onFinishTap: () -> Unit,
    onFinishNavigate: () -> Unit
) {
    LaunchedEffect(exerciseState) {
        if (exerciseState.isEnded) {
            Log.i(TAG, "* Finished state received")
            onFinishNavigate()
        }
    }

    var showTimer by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(1)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        VerticalPagerIndicator(
            pagerState = pagerState,
            activeColor = if (!exerciseState.isPaused) {
                MaterialTheme.colors.onSurface
            } else {
                MaterialTheme.colors.secondary
            },
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
        when (screenList[page].screenFormat) {
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

