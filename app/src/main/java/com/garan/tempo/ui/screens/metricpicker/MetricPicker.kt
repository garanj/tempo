package com.garan.tempo.ui.screens.metricpicker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.data.metrics.TempoMetric
import com.google.android.horologist.compose.navscaffold.scrollableColumn
import kotlinx.coroutines.launch


@Composable
fun MetricPicker(
    onClick: suspend (TempoMetric) -> Unit,
    metrics: MetricPickerUiState,
    scrollState: ScalingLazyListState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val sortedMetrics = remember(metrics) {
        metrics.tempoMetrics.toList().sortedBy {
            context.getString(it.displayNameId)
        }
    }
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
        items(sortedMetrics) { metric ->
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                label = { Text(stringResource(id = metric.displayNameId)) },
                onClick = {
                    coroutineScope.launch {
                        onClick(metric)
                    }
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}