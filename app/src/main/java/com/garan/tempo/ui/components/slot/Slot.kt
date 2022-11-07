package com.garan.tempo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import com.garan.tempo.data.AvailabilityHolder
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.ui.components.ambient.AmbientState
import com.garan.tempo.ui.components.slot.ActiveDurationSlot
import com.garan.tempo.ui.components.slot.WorkoutSlot
import com.garan.tempo.ui.screens.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_WIDTH_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_SHOW_BACKGROUND
import com.garan.tempo.ui.screens.WEAR_PREVIEW_UI_MODE
import com.garan.tempo.ui.theme.TempoTheme

@Composable
fun Slot(
    metricType: TempoMetric?,
    metricValue: Number?,
    state: ExerciseState,
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?,
    ambientState: AmbientState,
    textAlign: TextAlign = TextAlign.End,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false,
    availabilityHolder: AvailabilityHolder
) {
    val configClick = remember { { onConfigClick() } }
    val value = metricValue?.toLong() ?: 0L

    if (metricType != null && metricType == TempoMetric.ACTIVE_DURATION) {
        ActiveDurationSlot(
            checkpoint = checkpoint,
            state = state,
            textAlign = textAlign,
            onConfigClick = configClick,
            isForConfig = isForConfig,
            ambientState = ambientState
        )
    } else if (ambientState == AmbientState.Interactive) {
        WorkoutSlot(
            metricType = metricType,
            metricValue = value,
            isPaused = state.isPaused,
            textAlign = textAlign,
            onConfigClick = configClick,
            isForConfig = isForConfig,
            availabilityHolder = availabilityHolder
        )
    }
}


@Preview(
    widthDp = WEAR_PREVIEW_DEVICE_WIDTH_DP,
    heightDp = WEAR_PREVIEW_DEVICE_HEIGHT_DP,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun WorkoutMetricPreview() {
    TempoTheme {
        WorkoutSlot(
            metricType = TempoMetric.DISTANCE,
            metricValue = 1500,
            isPaused = false,
            textAlign = TextAlign.Start,
            onConfigClick = {},
            availabilityHolder = AvailabilityHolder.ALL_AVAILABLE
        )
    }
}
