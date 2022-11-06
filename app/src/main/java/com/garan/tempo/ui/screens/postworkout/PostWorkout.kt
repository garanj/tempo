package com.garan.tempo.ui.screens.postworkout

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.items
import coil.compose.rememberAsyncImagePainter
import com.garan.tempo.R
import com.garan.tempo.data.SavedExercise
import com.garan.tempo.data.SavedExerciseWithMetrics
import com.garan.tempo.ui.components.SummaryMetricChip
import com.garan.tempo.ui.screens.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_DEVICE_WIDTH_DP
import com.garan.tempo.ui.screens.WEAR_PREVIEW_SHOW_BACKGROUND
import com.garan.tempo.ui.screens.WEAR_PREVIEW_UI_MODE
import com.garan.tempo.ui.theme.TempoTheme
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import java.io.File
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun PostWorkoutScreen(
    savedExerciseWithMetrics: SavedExerciseWithMetrics,
    scrollState: ScalingLazyListState
) {
    val focusRequester = remember { FocusRequester() }
    ScalingLazyColumn(
        modifier = Modifier.scrollableColumn(
            scrollableState = scrollState,
            focusRequester = focusRequester
        ),
        state = scrollState,
        anchorType = ScalingLazyListAnchorType.ItemStart,
        autoCentering = AutoCenteringParams()
    ) {
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        val savedExercise = savedExerciseWithMetrics.savedExercise
        val metrics = savedExerciseWithMetrics.savedExerciseMetrics
        item {
            SummaryMetricChip(
                labelId = R.string.summary_metric_active_duration,
                metricText = DateUtils.formatElapsedTime(
                    savedExercise.activeDuration.seconds
                )
            )
        }
        item {
            SummaryMetricChip(
                labelId = R.string.summary_metric_start_time,
                formatter.format(savedExercise.startTime)
            )
        }
        items(metrics) { metric ->
            SummaryMetricChip(
                labelId = metric.metric.displayNameId,
                metric = metric.metric,
                value = metric.value
            )
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
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
            SavedExerciseWithMetrics(
                savedExercise = SavedExercise(
                    exerciseId = 1,
                    recordingId = "1234",
                    startTime = ZonedDateTime.now(),
                    activeDuration = Duration.ofMinutes(30)
                ),
                savedExerciseMetrics = listOf()
            ),
            ScalingLazyListState()
        )
    }
}
