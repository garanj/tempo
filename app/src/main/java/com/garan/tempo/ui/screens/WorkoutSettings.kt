package com.garan.tempo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import com.garan.tempo.Screen
import com.garan.tempo.TAG
import com.garan.tempo.UiState
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.settings.Units
import com.garan.tempo.settings.defaults.defaultExerciseSettingsList
import com.garan.tempo.ui.components.AutoPauseToggle
import com.garan.tempo.ui.model.WorkoutSettingsViewModel
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun WorkoutSettingsScreen(
    onScreenButtonClick: () -> Unit,
    viewModel: WorkoutSettingsViewModel = hiltViewModel<WorkoutSettingsViewModel>()
) {
    val uiState by viewModel.uiState

    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 10.dp,
            end = 20.dp,
            bottom = 30.dp
        )
    ) {
        item {
            Text(
                text = uiState.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.title2,
                color = MaterialTheme.colors.onBackground
            )
        }
        if (uiState.supportsAutoPause or true) {
            item {
                AutoPauseToggle(false, {})
            }
        }
        item {
            Chip(
                onClick = onScreenButtonClick,
                label = { Text("Edit screens") },
            )
        }
    }
}