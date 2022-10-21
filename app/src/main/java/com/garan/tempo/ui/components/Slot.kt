package com.garan.tempo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.ui.format.LocalDisplayUnitFormatter
import com.garan.tempo.ui.metrics.TempoMetric
import com.garan.tempo.ui.screens.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_WIDTH_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_SHOW_BACKGROUND
import com.garan.tempo.ui.screens.WEAR_PREVIEW_UI_MODE
import com.garan.tempo.ui.screens.workout.LocalDataAvailability
import com.garan.tempo.ui.theme.TempoTheme
import kotlinx.coroutines.delay
import java.time.Duration

@Composable
fun Slot(
    metricType: TempoMetric?,
    metricValue: Number?,
    state: ExerciseState,
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?,
    textAlign: TextAlign = TextAlign.End,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false
) {
    //Log.i(TAG, "Slot: $metricType ")
    val configClick = remember { { onConfigClick() } }
    val value = metricValue?.toLong() ?: 0L

    if (metricType != null && metricType == TempoMetric.ACTIVE_DURATION) {
        ActiveDurationMetric(
            checkpoint = checkpoint,
            state = state,
            textAlign = textAlign,
            onConfigClick = configClick,
            isForConfig = isForConfig
        )
    } else {
        WorkoutMetric(
            metricType = metricType,
            metricValue = value,
            isPaused = state.isPaused,
            textAlign = textAlign,
            onConfigClick = configClick,
            isForConfig = isForConfig
        )
    }
}

private fun calculateDurationMillis(checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?) =
    checkpoint?.activeDuration
        ?.plus(
            Duration.ofMillis(System.currentTimeMillis() - checkpoint.time.toEpochMilli())
        )?.toMillis() ?: 0L

@Composable
fun ActiveDurationMetric(
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?,
    state: ExerciseState,
    textAlign: TextAlign,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false
) {
    var durationStart by remember { mutableStateOf(calculateDurationMillis(checkpoint)) }
    var duration by remember { mutableStateOf(0L) }
    var tickStart by remember { mutableStateOf(System.currentTimeMillis()) }
    val seconds by remember { derivedStateOf { duration / 1000 } }

    LaunchedEffect(state) {
        durationStart = calculateDurationMillis(checkpoint)
        tickStart = System.currentTimeMillis()

        if (state == ExerciseState.ACTIVE) {
            while (true) {
                // TODO - need to take into account screen state
                delay(200)
                val delta = System.currentTimeMillis() - tickStart
                duration = durationStart + delta
            }
        }
    }

    val formattedValue = "%01d:%02d:%02d".format(
        seconds / 3600, (seconds % 3600) / 60, seconds % 60
    )
    AutoSizeText(
        text = formattedValue,
        textAlign = textAlign,
        mainColor = if (!state.isPaused) {
            MaterialTheme.colors.onSurface
        } else {
            MaterialTheme.colors.secondary
        },
        sizingPlaceholder = "8:88:88",
        onClick = onConfigClick,
        isForConfig = isForConfig
    )
}

@Composable
fun WorkoutMetric(
    metricType: TempoMetric?,
    metricValue: Number?,
    isPaused: Boolean,
    textAlign: TextAlign,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false
) {
    val formatter = LocalDisplayUnitFormatter.current
    val availability = LocalDataAvailability.current.heartRateAvailability
    val context = LocalContext.current

    val formattedValue by remember(metricType, metricValue) {
        val formattedString = if (metricType == TempoMetric.HEART_RATE_BPM &&
            availability != DataTypeAvailability.AVAILABLE
        ) {
            "--"
        } else if (metricType != null &&
            metricValue != null
        ) {
            formatter.formatValue(
                metricType,
                metricValue
            )
        } else {
            "--"
        }
        mutableStateOf(formattedString)
    }
    val label by remember(metricType) {
        val metricLabel = if (metricType != null) {
            context.getString(formatter.labelId(metricType))
        } else {
            ""
        }
        mutableStateOf(metricLabel)
    }
    val placeholder by remember {
        mutableStateOf(
            metricType?.placeholder ?: "00:00"
        )
    }
    AutoSizeText(
        text = formattedValue,
        textAlign = textAlign,
        mainColor = if (!isPaused) {
            MaterialTheme.colors.onSurface
        } else {
            MaterialTheme.colors.secondary
        },
        sizingPlaceholder = placeholder,
        unitText = label,
        onClick = onConfigClick,
        isForConfig = isForConfig
    )
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
        WorkoutMetric(
            metricType = TempoMetric.DISTANCE,
            metricValue = 1500,
            isPaused = false,
            textAlign = TextAlign.Start,
            onConfigClick = {}
        )
    }
}
