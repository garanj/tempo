package com.garan.tempo.ui.components.display

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import com.garan.tempo.ui.components.Slot
import com.garan.tempo.ui.metrics.TempoMetric
import java.util.EnumMap

@Composable
fun OneSlotDisplay(
    metricsConfig: List<TempoMetric>,
    metricsUpdate: EnumMap<TempoMetric, Number>,
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?,
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
            Slot(
                metricType = metricsConfig.getOrNull(0),
                metricValue = metricsUpdate[metricsConfig.getOrNull(0)],
                checkpoint = checkpoint,
                state = exerciseState,
                textAlign = TextAlign.Center,
                onConfigClick = { onConfigClick(0) },
                isForConfig = isForConfig
            )
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
fun OneSlotDisplayPreview() {
    val config = listOf(
        TempoMetric.PACE
    )
    val update = remember {
        mutableStateMapOf<TempoMetric, Number>(
            TempoMetric.PACE to 3.7
        )
    }
//    TempoTheme {
//        OneSlotDisplay(
//            metricsConfig = config,
//            metricsUpdate = update,
//            checkpoint = ExerciseUpdate.ActiveDurationCheckpoint(
//                Instant.now(), Duration.ofSeconds(15)
//            ),
//            exerciseState = ExerciseState.ACTIVE
//        )
//    }
}