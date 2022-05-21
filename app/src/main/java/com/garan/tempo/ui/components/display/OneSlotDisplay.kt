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
import androidx.health.services.client.data.Value
import com.garan.tempo.DisplayUpdateMap
import com.garan.tempo.ui.components.Slot
import com.garan.tempo.ui.metrics.DisplayMetric
import com.garan.tempo.ui.theme.TempoTheme

@Composable
fun OneSlotDisplay(
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
                    Slot(
                        metricsConfig.getOrNull(0),
                        metricsUpdate[metricsConfig.getOrNull(0)],
                        exerciseState,
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
        DisplayMetric.PACE
    )
    val update = remember {
        mutableStateMapOf(
            DisplayMetric.PACE to Value.ofDouble(3.7)
        )
    }
    TempoTheme {
        OneSlotDisplay(
            metricsConfig = config,
            metricsUpdate = update,
            exerciseState = ExerciseState.ACTIVE)
    }
}