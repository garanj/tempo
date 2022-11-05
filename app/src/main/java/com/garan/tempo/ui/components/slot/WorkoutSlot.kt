package com.garan.tempo.ui.components.slot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.health.services.client.data.DataTypeAvailability
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.ui.components.AutoSizeText
import com.garan.tempo.ui.format.LocalDisplayUnitFormatter
import com.garan.tempo.ui.screens.workout.LocalDataAvailability

@Composable
fun WorkoutSlot(
    metricType: TempoMetric?,
    metricValue: Number?,
    isPaused: Boolean,
    textAlign: TextAlign,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false
) {
    val formatter = LocalDisplayUnitFormatter.current
    val availability = LocalDataAvailability.current.heartRateAvailability
    val context = LocalContext.current

    val formattedValue by remember(metricType, metricValue) {
        val formattedString = if (metricType == TempoMetric.HEART_RATE_BPM &&
            availability != DataTypeAvailability.AVAILABLE
        ) {
            "--"
        } else if (metricType != null &&
            metricValue != null
        ) {
            formatter.formatValue(
                metricType,
                metricValue
            )
        } else {
            "--"
        }
        mutableStateOf(formattedString)
    }
    val label by remember(metricType) {
        val metricLabel = if (metricType != null) {
            context.getString(formatter.labelId(metricType))
        } else {
            ""
        }
        mutableStateOf(metricLabel)
    }
    val placeholder by remember {
        mutableStateOf(
            metricType?.placeholder ?: "00:00"
        )
    }
    AutoSizeText(
        text = formattedValue,
        textAlign = textAlign,
        mainColor = if (!isPaused) {
            MaterialTheme.colors.onSurface
        } else {
            MaterialTheme.colors.secondary
        },
        sizingPlaceholder = placeholder,
        unitText = label,
        onClick = onConfigClick,
        isForConfig = isForConfig
    )
}