package com.garan.tempo.ui.screens.workoutsettings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import com.garan.tempo.ui.components.AutoPauseToggle

@Composable
fun WorkoutSettingsScreen(
    onScreenButtonClick: () -> Unit,
    viewModel: WorkoutSettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.exerciseSettings.collectAsState(WorkoutSettingsUiState())

    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        autoCentering = AutoCenteringParams(0, 100),
        anchorType = ScalingLazyListAnchorType.ItemStart,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = settings.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.title2,
                color = MaterialTheme.colors.onBackground
            )
        }
        if (settings.supportsAutoPause) {
            item {
                AutoPauseToggle(settings.useAutoPause, {
                    viewModel.setAutoPause(!settings.useAutoPause)
                })
            }
        }
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                onClick = onScreenButtonClick,
                label = { Text("Edit screens") },
            )
        }
    }
}