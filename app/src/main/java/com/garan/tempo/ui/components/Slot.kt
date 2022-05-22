package com.garan.tempo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.Value
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.ui.format.LocalDisplayUnitFormatter
import com.garan.tempo.ui.metrics.DisplayMetric
import com.garan.tempo.ui.screens.WEAR_PREVIEW_API_LEVEL
import com.garan.tempo.ui.screens.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_WIDTH_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_SHOW_BACKGROUND
import com.garan.tempo.ui.screens.WEAR_PREVIEW_UI_MODE
import kotlinx.coroutines.delay

@Composable
fun Slot(
    metricType: DisplayMetric?,
    metricValue: Value?,
    state: ExerciseState,
    textAlign: TextAlign = TextAlign.End,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false
) {
    if (metricType != null && metricType == DisplayMetric.ACTIVE_DURATION) {
        ActiveDurationMetric(
            metricValue = metricValue,
            state = state,
            textAlign = textAlign,
            onConfigClick = onConfigClick,
            isForConfig = isForConfig
        )
    } else {
        WorkoutMetric(
            metricType = metricType,
            metricValue = metricValue,
            isPaused = state.isPaused,
            textAlign = textAlign,
            onConfigClick = onConfigClick,
            isForConfig = isForConfig
        )
    }
}

@Composable
fun ActiveDurationMetric(
    metricValue: Value?,
    state: ExerciseState,
    textAlign: TextAlign,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false
) {
    var durationStart by remember { mutableStateOf(metricValue?.asLong() ?: 0L) }
    var duration by remember { mutableStateOf(metricValue?.asLong() ?: 0L) }
    var tickStart by remember { mutableStateOf(System.currentTimeMillis() / 1000) }

    LaunchedEffect(state) {
        durationStart = metricValue?.asLong() ?: 0L
        tickStart = System.currentTimeMillis() / 1000

        if (state == ExerciseState.ACTIVE) {
            while (true) {
                delay(200)
                val delta = System.currentTimeMillis() / 1000 - tickStart
                duration = durationStart + delta
            }
        }
    }

    val formattedValue = "%01d:%02d:%02d".format(
        duration / 3600, (duration % 3600) / 60, duration % 60
    )
    AutoSizeText(
        text = formattedValue,
        textAlign = textAlign,
        mainColor = if (!state.isPaused) {
            MaterialTheme.colors.onSurface
        } else {
            MaterialTheme.colors.secondary
        },
        sizingPlaceholder = "0:00:00",
        unitText = "",
        onClick = onConfigClick,
        isForConfig = isForConfig
    )
}

@Composable
fun WorkoutMetric(
    metricType: DisplayMetric?,
    metricValue: Value?,
    isPaused: Boolean,
    textAlign: TextAlign,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false
) {
    val formatter = LocalDisplayUnitFormatter.current

    val formattedValue = if (metricType != null &&
        metricValue != null
    ) {
        formatter.formatValue(
            metricType,
            metricValue
        )
    } else {
        "--"
    }
    val label = if (metricType != null) {
        stringResource(formatter.labelId(metricType))
    } else {
        ""
    }
    val placeholder = metricType?.placeholder() ?: "00:00"
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
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun WorkoutMetricPreview() {
//    TempoTheme {
//        WorkoutMetric(0,
//            listOf(DisplayMetric.DISTANCE),
//            mapOf(DisplayMetric.DISTANCE to Value.ofDouble(1092.23))
//        )
//    }
}
