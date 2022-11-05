package com.garan.tempo.ui.screens.startmenu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseType
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import com.garan.tempo.R
import com.garan.tempo.data.imageVector
import com.garan.tempo.settings.ExerciseSettings
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.ui.theme.TempoTheme
import com.google.android.horologist.compose.navscaffold.scrollableColumn

/**
 * Composable functions used on the Connect screen, for initiating a connection to the fan.
 */
@Composable
fun StartMenuScreen(
    exerciseSettings: List<ExerciseSettingsWithScreens>,
    onPreWorkoutClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    scrollState: ScalingLazyListState
) {
    if (exerciseSettings.isNotEmpty()) {
        val focusRequester = remember { FocusRequester() }
        ScalingLazyColumn(
            modifier = Modifier.scrollableColumn(
                scrollableState = scrollState,
                focusRequester = focusRequester
            ),
            state = scrollState,
            anchorType = ScalingLazyListAnchorType.ItemStart,
            autoCentering = AutoCenteringParams()
        ) {
            items(exerciseSettings) { setting ->
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        setting.exerciseSettings.exerciseSettingsId?.let { id ->
                            onPreWorkoutClick(id)
                        }
                    },
                    label = { Text(setting.exerciseSettings.name) },
                    icon = {
                        Icon(
                            imageVector = setting.exerciseSettings.exerciseType.imageVector,
                            contentDescription = setting.exerciseSettings.name
                        )
                    }
                )
            }
            item {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSettingsClick,
                    label = { Text(text = stringResource(id = R.string.settings)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                )
            }
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun StarMenuPreview() {
    val scrollState = rememberScalingLazyListState(
        initialCenterItemIndex = 1
    )
    val settings = listOf(
        ExerciseSettingsWithScreens(
            exerciseSettings = ExerciseSettings(
                name = "Running",
                exerciseType = ExerciseType.RUNNING,
                useAutoPause = true,
                recordingMetrics = setOf(),
                endSummaryMetrics = setOf()
            ),
            screenSettings = listOf()
        ),
        ExerciseSettingsWithScreens(
            exerciseSettings = ExerciseSettings(
                name = "Cycling",
                exerciseType = ExerciseType.BIKING,
                useAutoPause = true,
                recordingMetrics = setOf(),
                endSummaryMetrics = setOf()
            ),
            screenSettings = listOf()
        )
    )
    TempoTheme {
        StartMenuScreen(
            exerciseSettings = settings,
            onPreWorkoutClick = {},
            onSettingsClick = { },
            scrollState = scrollState
        )
    }
}