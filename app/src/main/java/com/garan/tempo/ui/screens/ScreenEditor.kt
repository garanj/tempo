package com.garan.tempo.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.health.services.client.data.ExerciseState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.ui.components.SixSlotMetricDisplay
import com.garan.tempo.ui.components.display.OnePlusFourSlotDisplay
import com.garan.tempo.ui.components.display.OnePlusTwoSlotDisplay
import com.garan.tempo.ui.components.display.OneSlotDisplay
import com.garan.tempo.ui.components.display.TwoSlotDisplay
import com.garan.tempo.ui.metrics.screenEditorDefaults
import com.garan.tempo.ui.model.WorkoutSettingsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.VerticalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScreenEditor(
    viewModel: WorkoutSettingsViewModel = hiltViewModel<WorkoutSettingsViewModel>()
) {
    val pagerState = rememberPagerState(1)
    val uiState by viewModel.uiState
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

    val screens = uiState.screens
    val metricsUpdate = screenEditorDefaults()
    VerticalPager(
        modifier = Modifier.fillMaxSize(),
        count = uiState.screens.size,
        state = pagerState
    ) { page ->
        when(screens[page].screenFormat) {
            ScreenFormat.ONE_PLUS_FOUR_SLOT -> OnePlusFourSlotDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = ExerciseState.ACTIVE
            )
            ScreenFormat.SIX_SLOT -> SixSlotMetricDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = ExerciseState.ACTIVE
            )
            ScreenFormat.ONE_PLUS_TWO_SLOT -> OnePlusTwoSlotDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = ExerciseState.ACTIVE
            )
            ScreenFormat.TWO_SLOT -> TwoSlotDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = ExerciseState.ACTIVE
            )
            else -> OneSlotDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                exerciseState = ExerciseState.ACTIVE
            )
        }
    }
}