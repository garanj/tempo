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
fun SixSlotMetricDisplay(
    metricsConfig: List<DisplayMetric>,
    metricsUpdate: DisplayUpdateMap,
    exerciseState: ExerciseState,
    screenIndex: Int = 0,
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
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Slot(
                            metricsConfig.getOrNull(0),
                            metricsUpdate[metricsConfig.getOrNull(0)],
                            exerciseState,
                            TextAlign.Start,
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
                            metricsConfig.getOrNull(1),
                            metricsUpdate[metricsConfig.getOrNull(1)],
                            exerciseState,
                            onConfigClick = { onConfigClick(1) },
                            isForConfig = isForConfig
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Slot(
                            metricsConfig.getOrNull(2),
                            metricsUpdate[metricsConfig.getOrNull(2)],
                            exerciseState,
                            TextAlign.Start,
                            onConfigClick = { onConfigClick(2) },
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
                            metricsConfig.getOrNull(3),
                            metricsUpdate[metricsConfig.getOrNull(3)],
                            exerciseState,
                            onConfigClick = { onConfigClick(3) },
                            isForConfig = isForConfig
                        )
                    }
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
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Slot(
                            metricsConfig.getOrNull(4),
                            metricsUpdate[metricsConfig.getOrNull(4)],
                            exerciseState,
                            TextAlign.Start,
                            onConfigClick = { onConfigClick(4) },
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
                            metricsConfig.getOrNull(5),
                            metricsUpdate[metricsConfig.getOrNull(5)],
                            exerciseState,
                            onConfigClick = { onConfigClick(5) },
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
fun SixSlotMetricDisplayPreview() {
    val config = listOf(
        DisplayMetric.ACTIVE_DURATION,
        DisplayMetric.CALORIES,
        DisplayMetric.PACE,
        DisplayMetric.DISTANCE,
        DisplayMetric.HEART_RATE_BPM,
        DisplayMetric.AVG_PACE
    )
    val update = remember {
        mutableStateMapOf(
            DisplayMetric.ACTIVE_DURATION to Value.ofLong(73L),
            DisplayMetric.CALORIES to Value.ofDouble(176.1),
            DisplayMetric.PACE to Value.ofDouble(3.7),
            DisplayMetric.DISTANCE to Value.ofDouble(3281.0),
            DisplayMetric.HEART_RATE_BPM to Value.ofDouble(97.0),
            DisplayMetric.AVG_PACE to Value.ofDouble(3.6)
        )
    }
    TempoTheme {
        SixSlotMetricDisplay(
            metricsConfig = config,
            metricsUpdate = update,
            exerciseState = ExerciseState.ACTIVE
        )
    }
}