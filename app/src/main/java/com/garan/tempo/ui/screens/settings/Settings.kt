package com.garan.tempo.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.settings.TempoSettings
import com.garan.tempo.settings.Units
import com.garan.tempo.ui.components.UnitsToggle
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    exerciseSettings: List<ExerciseSettingsWithScreens>,
    tempoSettings: TempoSettings,
    onWorkoutSettingsClick: (Long) -> Unit,
    onSetUnits: (Units) -> Unit,
    scrollState: ScalingLazyListState
) {
    val scope = rememberCoroutineScope()
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
        item {
            UnitsToggle(
                units = tempoSettings.units,
                onCheckedChange = {
                    scope.launch(Dispatchers.IO) {
                        val newValue = when (it) {
                            true -> Units.IMPERIAL
                            else -> Units.METRIC
                        }
                        onSetUnits(newValue)
                    }
                }
            )
        }

        items(exerciseSettings) { item ->
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                label = { Text(item.exerciseSettings.name) },
                onClick = {
                    item.exerciseSettings.exerciseSettingsId?.let { id ->
                        onWorkoutSettingsClick(id)
                    }
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}