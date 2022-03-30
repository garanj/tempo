package com.garan.tempo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.Value
import com.garan.tempo.ui.components.display.OneSlotDisplay
import com.garan.tempo.ui.metrics.DisplayMetric
import com.garan.tempo.ui.theme.TempoTheme
import java.util.EnumSet

enum class BoxBorder {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
}

fun Modifier.boxBorder(
    color: Color = Color.Red,
    strokeWidth: Float = 1.0f,
    boxBorders: EnumSet<BoxBorder> = EnumSet.allOf(BoxBorder::class.java)
) = this.drawWithCache {
        onDrawWithContent {
            drawContent()
            if (boxBorders.contains(BoxBorder.TOP)) {
                drawLine(
                    color = color,
                    strokeWidth = strokeWidth,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f)
                )
            }
            if (boxBorders.contains(BoxBorder.BOTTOM)) {
                drawLine(
                    color = color,
                    strokeWidth = strokeWidth,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            }
            if (boxBorders.contains(BoxBorder.LEFT)) {
                drawLine(
                    color = color,
                    strokeWidth = strokeWidth,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height)
                )
            }
            if (boxBorders.contains(BoxBorder.RIGHT)) {
                drawLine(
                    color = color,
                    strokeWidth = strokeWidth,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height)
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
    TempoTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxSize(0.707f)
                    .boxBorder(
                        color = Color.Green,
                        boxBorders = EnumSet.of(BoxBorder.TOP, BoxBorder.BOTTOM)
                    )
            ) {

            }
        }
    }
}