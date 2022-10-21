package com.garan.tempo.ui.screens.workout

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.hilt.navigation.compose.hiltViewModel
import com.garan.tempo.TAG
import com.garan.tempo.data.AvailabilityHolder
import com.garan.tempo.data.metrics.TempoMetric
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
import com.google.accompanist.pager.rememberPagerState
import java.util.EnumMap

/**
 * Composable functions for use when connected to the fan, either when in HR-guided or non-HR mode.
 */
val LocalDataAvailability = compositionLocalOf { AvailabilityHolder() }

@Composable
fun WorkoutScreen(
    onFinishNavigate: (String) -> Unit,
    // TODO - remove viewmodel
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val serviceState by viewModel.serviceState

    // TODO - auto paused restart
    if (serviceState is ServiceState.Connected) {
        val service = serviceState as ServiceState.Connected
        val exerciseState by service.exerciseState
        val metrics by service.metrics
        val checkpoint by service.checkpoint
        val settings by service.settings
        val exerciseId by service.exerciseId
        val availability by service.availability
        CompositionLocalProvider(LocalDataAvailability provides availability) {
            ActiveScreen(
                metricsUpdate = metrics,
                checkpoint = checkpoint,
                exerciseState = exerciseState,
                screenList = settings?.screenSettings ?: listOf(),
                onFinishTap = {
                    viewModel.endExercise()
                },
                onPauseTap = {
                    viewModel.pauseResumeExercise()
                },
                onFinishNavigate = {
                    exerciseId?.let {
                        onFinishNavigate(it.toString())
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ActiveScreen(
    metricsUpdate: EnumMap<TempoMetric, Number>,
    screenList: List<ScreenSettings>,
    exerciseState: ExerciseState,
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?,
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
                checkpoint = checkpoint,
                exerciseState = exerciseState
            )

            ScreenFormat.SIX_SLOT -> SixSlotMetricDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = checkpoint,
                exerciseState = exerciseState
            )

            ScreenFormat.ONE_PLUS_TWO_SLOT -> OnePlusTwoSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = checkpoint,
                exerciseState = exerciseState
            )

            ScreenFormat.TWO_SLOT -> TwoSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = checkpoint,
                exerciseState = exerciseState
            )

            else -> OneSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = checkpoint,
                exerciseState = exerciseState
            )
        }
    }
    if (showTimer) {
        EndRing(onFinishTap = onFinishTap)
    }
}

