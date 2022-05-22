package com.garan.tempo.ui.components.display

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.Value
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.DisplayUpdateMap
import com.garan.tempo.ui.components.BoxBorder
import com.garan.tempo.ui.components.Slot
import com.garan.tempo.ui.components.boxBorder
import com.garan.tempo.ui.metrics.DisplayMetric
import com.garan.tempo.ui.theme.TempoTheme
import java.util.EnumSet

@Composable
fun OnePlusFourSlotDisplay(
    metricsConfig: List<DisplayMetric>,
    metricsUpdate: DisplayUpdateMap,
    exerciseState: ExerciseState,
    onConfigClick: (Int) -> Unit = { _ -> },
    isForConfig: Boolean = false
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(0.707f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .boxBorder(
                            color = if (!exerciseState.isPaused) {
                                MaterialTheme.colors.onSurface
                            } else {
                                MaterialTheme.colors.secondary
                            },
                            boxBorders = EnumSet.of(BoxBorder.BOTTOM)
                        )
                        .weight(2f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Slot(
                            metricType = metricsConfig.getOrNull(0),
                            metricValue = metricsUpdate[metricsConfig.getOrNull(0)],
                            state = exerciseState,
                            textAlign = TextAlign.Start,
                            onConfigClick = { onConfigClick(0) },
                            isForConfig = isForConfig
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Slot(
                            metricType = metricsConfig.getOrNull(1),
                            metricValue = metricsUpdate[metricsConfig.getOrNull(1)],
                            state = exerciseState,
                            onConfigClick = { onConfigClick(1) },
                            isForConfig = isForConfig
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slot(
                        metricType = metricsConfig.getOrNull(2),
                        metricValue = metricsUpdate[metricsConfig.getOrNull(2)],
                        state = exerciseState,
                        textAlign = TextAlign.Center,
                        onConfigClick = { onConfigClick(2) },
                        isForConfig = isForConfig
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .boxBorder(
                            color = if (!exerciseState.isPaused) {
                                MaterialTheme.colors.onSurface
                            } else {
                                MaterialTheme.colors.secondary
                            },
                            boxBorders = EnumSet.of(BoxBorder.TOP)
                        )
                        .weight(2f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Slot(
                            metricType = metricsConfig.getOrNull(3),
                            metricValue = metricsUpdate[metricsConfig.getOrNull(3)],
                            state = exerciseState,
                            textAlign = TextAlign.Start,
                            onConfigClick = { onConfigClick(3) },
                            isForConfig = isForConfig
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Slot(
                            metricType = metricsConfig.getOrNull(4),
                            metricValue = metricsUpdate[metricsConfig.getOrNull(4)],
                            state = exerciseState,
                            onConfigClick = { onConfigClick(4) },
                            isForConfig = isForConfig
                        )
                    }
                }
            }
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
fun OnePlusFourSlotDisplayPreview() {
    val config = listOf(
        DisplayMetric.ACTIVE_DURATION,
        DisplayMetric.CALORIES,
        DisplayMetric.PACE,
        DisplayMetric.DISTANCE,
        DisplayMetric.AVG_PACE
    )
    val update = remember {
        mutableStateMapOf(
            DisplayMetric.ACTIVE_DURATION to Value.ofLong(73L),
            DisplayMetric.CALORIES to Value.ofDouble(176.1),
            DisplayMetric.PACE to Value.ofDouble(3.7),
            DisplayMetric.DISTANCE to Value.ofDouble(3281.0),
            DisplayMetric.AVG_PACE to Value.ofDouble(3.6),
        )
    }
    TempoTheme {
        OnePlusFourSlotDisplay(
            metricsConfig = config,
            metricsUpdate = update,
            exerciseState = ExerciseState.ACTIVE
        )
    }
}