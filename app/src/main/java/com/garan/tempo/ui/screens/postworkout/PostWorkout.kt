package com.garan.tempo.ui.screens.postworkout

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import coil.compose.rememberAsyncImagePainter
import com.garan.tempo.R
import com.garan.tempo.data.SavedExercise
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.ui.components.SummaryMetricChip
import com.garan.tempo.ui.screens.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_WIDTH_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_SHOW_BACKGROUND
import com.garan.tempo.ui.screens.WEAR_PREVIEW_UI_MODE
import com.garan.tempo.ui.theme.TempoTheme
import java.io.File
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PostWorkoutScreen(
    savedExercise: SavedExercise,
    scrollState: ScalingLazyListState
) {
    ScalingLazyColumn(
        state = scrollState
    ) {
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        item {
            SummaryMetricChip(
                labelId = R.string.summary_metric_active_duration,
                metricText = DateUtils.formatElapsedTime(
                    savedExercise.activeDuration?.seconds ?: 0L
                )
            )
        }
        item {
            SummaryMetricChip(
                labelId = R.string.summary_metric_start_time,
                formatter.format(savedExercise.startTime)
            )
        }
        savedExercise.totalDistance?.let { totalDistance ->
            item {
                SummaryMetricChip(
                    labelId = R.string.summary_metric_total_distance,
                    metric = TempoMetric.DISTANCE,
                    value = totalDistance
                )
            }
        }
        savedExercise.totalCalories?.let { totalCalories ->
            item {
                SummaryMetricChip(
                    labelId = R.string.summary_metric_total_calories,
                    metric = TempoMetric.CALORIES,
                    value = totalCalories
                )
            }
        }
        savedExercise.avgPace?.let { avgPace ->
            item {
                SummaryMetricChip(
                    labelId = R.string.summary_metric_avg_pace,
                    metric = TempoMetric.AVG_PACE,
                    value = avgPace
                )
            }
        }
        savedExercise.avgHeartRate?.let { avgHeartRate ->
            item {
                SummaryMetricChip(
                    labelId = R.string.summary_metric_avg_heart_rate,
                    metric = TempoMetric.AVG_HEART_RATE,
                    value = avgHeartRate
                )
            }
        }
        if (savedExercise.hasMap) {
            item {
                val file = File(LocalContext.current.filesDir, "${savedExercise.exerciseId}.png")
                Image(
                    modifier = Modifier.fillParentMaxSize(),
                    painter = rememberAsyncImagePainter(file),
                    contentDescription = "...",
                    contentScale = ContentScale.Fit
                )
            }
        }
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
fun PostWorkoutScreenPreview() {
    TempoTheme {
        PostWorkoutScreen(
            SavedExercise(
                exerciseId = "1234",
                startTime = ZonedDateTime.now(),
                activeDuration = Duration.ofMinutes(30)
            ),
            ScalingLazyListState()
        )
    }
}
