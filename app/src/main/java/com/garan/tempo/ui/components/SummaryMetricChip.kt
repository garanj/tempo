package com.garan.tempo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.ui.format.LocalDisplayUnitFormatter

@Composable
fun SummaryMetricChip(
    labelId: Int,
    metricText: String
) {
    Chip(
        modifier = Modifier.fillMaxWidth(),
        colors = ChipDefaults.secondaryChipColors(),
        label = {
            Text(
                text = stringResource(id = labelId),
                style = MaterialTheme.typography.caption3
            )
        },
        secondaryLabel = {
            Text(
                text = metricText,
                style = MaterialTheme.typography.button
            )
        },
        onClick = {}
    )
}

@Composable
fun SummaryMetricChip(
    labelId: Int,
    metric: TempoMetric,
    value: Number
) {
    val formatter = LocalDisplayUnitFormatter.current
    val metricText = formatter.formatValue(metric, value)
    val metricUnits = stringResource(id = formatter.labelId(metric))
    SummaryMetricChip(
        labelId = labelId,
        metricText = "$metricText $metricUnits"
    )
}