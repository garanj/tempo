package com.garan.tempo.ui.screens.workout

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import com.garan.tempo.R
import com.garan.tempo.data.isAutoPauseState
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.isUserPaused
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.settings.ScreenSettings
import com.garan.tempo.ui.components.EndRing
import com.garan.tempo.ui.components.PauseLabel
import com.garan.tempo.ui.components.ambient.AmbientState
import com.garan.tempo.ui.components.display.OnePlusFourSlotDisplay
import com.garan.tempo.ui.components.display.OnePlusTwoSlotDisplay
import com.garan.tempo.ui.components.display.OneSlotDisplay
import com.garan.tempo.ui.components.display.SixSlotMetricDisplay
import com.garan.tempo.ui.components.display.TwoSlotDisplay
import com.garan.tempo.ui.theme.TempoTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.util.EnumMap
import kotlin.math.sign

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ActiveScreen(
    metricsUpdate: EnumMap<TempoMetric, Number>,
    screenList: List<ScreenSettings>,
    exerciseState: ExerciseState,
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?,
    onPauseResumeTap: () -> Unit,
    onFinishTap: () -> Unit,
    onFinishStateChange: () -> Unit,
    onActiveScreenChange: () -> Unit,
    ambientState: AmbientState
) {
    LaunchedEffect(exerciseState) {
        if (exerciseState.isEnded) {
            onFinishStateChange()
        }
    }
    val coroutineScope = rememberCoroutineScope()
    var showTimer by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(1)
    val focusRequester = remember { FocusRequester() }

    VerticalPager(
        modifier = Modifier
            .fillMaxSize()
            .onRotaryInputAccumulated {
                coroutineScope.launch {
                    pagerState.scrollToPage(
                        (pagerState.currentPage + it.sign.toInt()).coerceIn(
                            0,
                            pagerState.pageCount - 1
                        )
                    )
                    onActiveScreenChange()
                }
            }
            .focusRequester(focusRequester)
            .focusable()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        showTimer = true
                        tryAwaitRelease()
                        showTimer = false
                    },
                    onDoubleTap = {
                        onPauseResumeTap()
                    },
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
                exerciseState = exerciseState,
                ambientState = ambientState
            )

            ScreenFormat.SIX_SLOT -> SixSlotMetricDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = checkpoint,
                exerciseState = exerciseState,
                ambientState = ambientState
            )

            ScreenFormat.ONE_PLUS_TWO_SLOT -> OnePlusTwoSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = checkpoint,
                exerciseState = exerciseState,
                ambientState = ambientState
            )

            ScreenFormat.TWO_SLOT -> TwoSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = checkpoint,
                exerciseState = exerciseState,
                ambientState = ambientState
            )

            else -> OneSlotDisplay(
                metricsConfig = screenList[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = checkpoint,
                exerciseState = exerciseState,
                ambientState = ambientState
            )
        }
    }
    if (showTimer) {
        EndRing(onFinishTap = onFinishTap)
    }
    if (exerciseState.isAutoPauseState()) {
        PauseLabel(stringResource(id = R.string.auto_paused))
    } else if (exerciseState.isUserPaused) {
        PauseLabel(stringResource(id = R.string.user_paused))
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ActiveScreenPreview() {
    TempoTheme {
        ActiveScreen(
            metricsUpdate = EnumMap(
                mapOf(
                    TempoMetric.DISTANCE to 1000.0
                )
            ),
            screenList = listOf(
                ScreenSettings(
                    screenSettingsId = 1,
                    screenIndex = 0,
                    screenFormat = ScreenFormat.ONE_SLOT,
                    metrics = listOf(
                        TempoMetric.DISTANCE
                    )
                )
            ),
            checkpoint = ExerciseUpdate.ActiveDurationCheckpoint(
                time = Instant.now(),
                activeDuration = Duration.ofSeconds(150)
            ),
            exerciseState = ExerciseState.AUTO_PAUSED,
            onPauseResumeTap = {},
            onFinishTap = {},
            onFinishStateChange = {},
            onActiveScreenChange = {},
            ambientState = AmbientState.Interactive
        )
    }
}