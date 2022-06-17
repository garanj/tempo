package com.garan.tempo.ui.screens.postworkout

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.Value
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import com.garan.tempo.R
import com.garan.tempo.data.SavedExercise
import com.garan.tempo.ui.components.SummaryMetricChip
import com.garan.tempo.ui.metrics.DisplayMetric
import com.garan.tempo.ui.screens.WEAR_PREVIEW_API_LEVEL
import com.garan.tempo.ui.screens.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_WIDTH_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_SHOW_BACKGROUND
import com.garan.tempo.ui.screens.WEAR_PREVIEW_UI_MODE
import com.garan.tempo.ui.theme.TempoTheme
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PostWorkoutScreen(
    savedExercise: SavedExercise
) {
    ScalingLazyColumn(
        anchorType = ScalingLazyListAnchorType.ItemStart,
        autoCentering = AutoCenteringParams()
    ) {
        // TODO - remember
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        item {
            SummaryMetricChip(
                labelId = R.string.summary_metric_active_duration,
                metricText = DateUtils.formatElapsedTime(
                        savedExercise.activeDuration?.seconds ?: 0L)
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
                    metric = DisplayMetric.DISTANCE,
                    value = Value.ofDouble(totalDistance)
                )
            }
        }
        savedExercise.totalCalories?.let { totalCalories ->
            item {
                SummaryMetricChip(
                    labelId = R.string.summary_metric_total_calories,
                    metric = DisplayMetric.CALORIES,
                    value = Value.ofDouble(totalCalories)
                )
            }
        }
        savedExercise.avgPace?.let { avgPace ->
            item {
                SummaryMetricChip(
                    labelId = R.string.summary_metric_avg_pace,
                    metric = DisplayMetric.AVG_PACE,
                    value = Value.ofDouble(avgPace)
                )
            }
        }
        savedExercise.avgHeartRate?.let { avgHeartRate ->
            item {
                SummaryMetricChip(
                    labelId = R.string.summary_metric_avg_heart_rate,
                    metric = DisplayMetric.AVG_HEART_RATE,
                    value = Value.ofDouble(avgHeartRate)
                )
            }
        }
    }
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
fun PostWorkoutScreenPreview() {
    TempoTheme {
        PostWorkoutScreen(
            SavedExercise(
                exerciseId = "1234",
                startTime = ZonedDateTime.now(),
                activeDuration = Duration.ofMinutes(30)
            )
        )
    }
}
