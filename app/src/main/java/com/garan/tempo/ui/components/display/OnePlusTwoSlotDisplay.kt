package com.garan.tempo.ui.components.display

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.data.AvailabilityHolder
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.ui.components.BoxBorder
import com.garan.tempo.ui.components.Slot
import com.garan.tempo.ui.components.ambient.AmbientState
import com.garan.tempo.ui.components.boxBorder
import com.garan.tempo.ui.theme.TempoTheme
import java.time.Duration
import java.time.Instant
import java.util.EnumMap
import java.util.EnumSet

@Composable
fun OnePlusTwoSlotDisplay(
    metricsConfig: List<TempoMetric>,
    metricsUpdate: EnumMap<TempoMetric, Number>,
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?,
    exerciseState: ExerciseState,
    ambientState: AmbientState,
    onConfigClick: (Int) -> Unit = { _ -> },
    isForConfig: Boolean = false,
    availabilityHolder: AvailabilityHolder
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
                        .weight(5f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slot(
                        metricType = metricsConfig.getOrNull(0),
                        metricValue = metricsUpdate[metricsConfig.getOrNull(0)],
                        checkpoint = checkpoint,
                        state = exerciseState,
                        textAlign = TextAlign.Center,
                        onConfigClick = { onConfigClick(0) },
                        isForConfig = isForConfig,
                        ambientState = ambientState,
                        availabilityHolder = availabilityHolder
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
                        .weight(3f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Slot(
                            metricType = metricsConfig.getOrNull(1),
                            metricValue = metricsUpdate[metricsConfig.getOrNull(1)],
                            checkpoint = checkpoint,
                            state = exerciseState,
                            textAlign = TextAlign.Start,
                            onConfigClick = { onConfigClick(1) },
                            isForConfig = isForConfig,
                            ambientState = ambientState,
                            availabilityHolder = availabilityHolder
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Slot(
                            metricType = metricsConfig.getOrNull(2),
                            metricValue = metricsUpdate[metricsConfig.getOrNull(2)],
                            checkpoint = checkpoint,
                            state = exerciseState,
                            onConfigClick = { onConfigClick(2) },
                            isForConfig = isForConfig,
                            ambientState = ambientState,
                            availabilityHolder = availabilityHolder
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
fun OnePlusTwoSlotDisplayPreview() {
    val config = listOf(
        TempoMetric.ACTIVE_DURATION,
        TempoMetric.CALORIES,
        TempoMetric.PACE
    )
    val update = EnumMap(
        mapOf<TempoMetric, Number>(
            TempoMetric.CALORIES to 176.1,
            TempoMetric.PACE to 3.7
        )
    )
    TempoTheme {
        OnePlusTwoSlotDisplay(
            metricsConfig = config,
            metricsUpdate = update,
            checkpoint = ExerciseUpdate.ActiveDurationCheckpoint(
                Instant.now(), Duration.ofSeconds(15)
            ),
            exerciseState = ExerciseState.ACTIVE,
            ambientState = AmbientState.Interactive,
            availabilityHolder = AvailabilityHolder.ALL_AVAILABLE
        )
    }
}