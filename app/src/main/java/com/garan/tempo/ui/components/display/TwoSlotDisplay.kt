package com.garan.tempo.ui.components.display

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
fun TwoSlotDisplay(
    metricsConfig: List<DisplayMetric>,
    metricsUpdate: DisplayUpdateMap,
    exerciseState: ExerciseState
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
                            color = MaterialTheme.colors.primary,
                            boxBorders = EnumSet.of(BoxBorder.BOTTOM)
                        )
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slot(
                        metricsConfig.getOrNull(0),
                        metricsUpdate[metricsConfig.getOrNull(0)],
                        exerciseState,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .boxBorder(
                            color = MaterialTheme.colors.primary,
                            boxBorders = EnumSet.of(BoxBorder.TOP)
                        )
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slot(
                        metricsConfig.getOrNull(1),
                        metricsUpdate[metricsConfig.getOrNull(1)],
                        exerciseState,
                        textAlign = TextAlign.Center
                    )
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
fun TwoSlotDisplayPreview() {
    val config = listOf(
        DisplayMetric.ACTIVE_DURATION,
        DisplayMetric.PACE
    )
    val update = remember {
        mutableStateMapOf(
            DisplayMetric.ACTIVE_DURATION to Value.ofLong(73L),
            DisplayMetric.PACE to Value.ofDouble(3.7)
        )
    }
    TempoTheme {
        TwoSlotDisplay(
            metricsConfig = config,
            metricsUpdate = update,
            exerciseState = ExerciseState.ACTIVE)
    }
}