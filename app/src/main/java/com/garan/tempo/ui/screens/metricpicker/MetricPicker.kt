package com.garan.tempo.ui.screens.metricpicker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.ui.metrics.TempoMetric


@Composable
fun MetricPicker(
    onClick: (TempoMetric) -> Unit,
    viewModel: MetricPickerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val metrics by viewModel.displayMetrics.collectAsState(initial = MetricPickerUiState())

    val sortedMetrics = metrics.tempoMetrics.toList().sortedBy {
        context.getString(it.displayNameId)
    }
    ScalingLazyColumn(
        autoCentering = AutoCenteringParams(),
        anchorType = ScalingLazyListAnchorType.ItemStart
    ) {
        items(sortedMetrics) { metric ->
            CompactChip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                label = { Text(stringResource(id = metric.displayNameId)) },
                onClick = {
                    onClick(metric)
                }
            )
        }
    }
}