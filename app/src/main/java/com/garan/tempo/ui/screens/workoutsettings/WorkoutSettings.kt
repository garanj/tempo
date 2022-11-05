package com.garan.tempo.ui.screens.workoutsettings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import com.garan.tempo.ui.components.AutoPauseToggle
import com.google.android.horologist.compose.navscaffold.scrollableColumn

@Composable
fun WorkoutSettingsScreen(
    onScreenButtonClick: () -> Unit,
    settings: WorkoutSettingsUiState,
    scrollState: ScalingLazyListState,
    onAutoPauseToggle: (Boolean) -> Unit
) {
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
            Text(
                text = settings.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.title2,
                color = MaterialTheme.colors.onBackground
            )
        }
        if (settings.supportsAutoPause) {
            item {
                AutoPauseToggle(settings.useAutoPause) {
                    onAutoPauseToggle(it)
                }
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
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}