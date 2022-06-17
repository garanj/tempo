package com.garan.tempo.ui.screens.postworkout

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.data.SavedExercise
import com.garan.tempo.ui.format.DisplayUnitFormatter
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
        val metricFormatter = DisplayUnitFormatter()
        item {
            Chip(
                label = {
                    Text(text = DateUtils.formatElapsedTime(
                        savedExercise.activeDuration?.seconds ?: 0L)
                    )
                },
                secondaryLabel = {
                    Text(text = "Active duration")
                },
                onClick = {}
            )
        }
        item {
            Chip(
                label = {
                    Text(text = formatter.format(
                            savedExercise.startTime
                        )
                    )
                },
                secondaryLabel = {
                    Text(text = "Start time")
                },
                onClick = {}
            )
        }
//        items(savedExercise.metrics) { metric ->
//            val label = stringResource(id = metric.metric.displayNameId())
//            val value = metricFormatter.formatValue(
//                metric.metric,
//                metric.value
//            )
//            Chip(
//                label = { Text(text = value) },
//                secondaryLabel = { Text(text = label) },
//                onClick = {}
//            )
//        }
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
