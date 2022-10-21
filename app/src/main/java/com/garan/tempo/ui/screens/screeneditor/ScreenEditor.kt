package com.garan.tempo.ui.screens.screeneditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewCozy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.R
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.ui.components.display.OnePlusFourSlotDisplay
import com.garan.tempo.ui.components.display.OnePlusTwoSlotDisplay
import com.garan.tempo.ui.components.display.OneSlotDisplay
import com.garan.tempo.ui.components.display.SixSlotMetricDisplay
import com.garan.tempo.ui.components.display.TwoSlotDisplay
import com.garan.tempo.ui.metrics.TempoMetric
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.VerticalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScreenEditor(
    onConfigClick: (Int, Int) -> Unit,
    onScreenFormatClick: (Int) -> Unit,
    viewModel: ScreenEditorViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(1)
    val settings by viewModel.exerciseSettings.collectAsState(ExerciseSettingsWithScreens())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        VerticalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colors.onSurface,
            inactiveColor = MaterialTheme.colors.secondary
        )
    }

    val screens = settings.screenSettings
    val metricsUpdate = TempoMetric.screenEditorDefaults
    val defaultCheckpoint = ExerciseUpdate.ActiveDurationCheckpoint(
        Instant.now(), Duration.ofSeconds(95)
    )
    VerticalPager(
        modifier = Modifier.fillMaxSize(),
        count = screens.size,
        state = pagerState
    ) { page ->
        when (screens[page].screenFormat) {
            ScreenFormat.ONE_PLUS_FOUR_SLOT -> OnePlusFourSlotDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = defaultCheckpoint,
                exerciseState = ExerciseState.USER_PAUSED,
                onConfigClick = { slot -> onConfigClick(page, slot) },
                isForConfig = true
            )

            ScreenFormat.SIX_SLOT -> SixSlotMetricDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = defaultCheckpoint,
                exerciseState = ExerciseState.USER_PAUSED,
                onConfigClick = { slot -> onConfigClick(page, slot) },
                isForConfig = true
            )

            ScreenFormat.ONE_PLUS_TWO_SLOT -> OnePlusTwoSlotDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = defaultCheckpoint,
                exerciseState = ExerciseState.USER_PAUSED,
                onConfigClick = { slot -> onConfigClick(page, slot) },
                isForConfig = true
            )

            ScreenFormat.TWO_SLOT -> TwoSlotDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = defaultCheckpoint,
                exerciseState = ExerciseState.USER_PAUSED,
                onConfigClick = { slot -> onConfigClick(page, slot) },
                isForConfig = true
            )

            else -> OneSlotDisplay(
                metricsConfig = screens[page].metrics,
                metricsUpdate = metricsUpdate,
                checkpoint = defaultCheckpoint,
                exerciseState = ExerciseState.USER_PAUSED,
                onConfigClick = { slot -> onConfigClick(page, slot) },
                isForConfig = true
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            IconButton(onClick = {
                onScreenFormatClick(page)
            }) {
                Icon(
                    imageVector = Icons.Default.ViewCozy,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        }
    }
}