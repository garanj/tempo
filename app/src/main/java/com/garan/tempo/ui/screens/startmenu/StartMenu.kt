package com.garan.tempo.ui.screens.startmenu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.R
import com.garan.tempo.data.imageVector
import com.garan.tempo.settings.ExerciseSettingsWithScreens

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
        ScalingLazyColumn(
            state = scrollState
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
    }
}