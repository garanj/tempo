package com.garan.tempo.ui.metrics

import androidx.compose.runtime.mutableStateMapOf
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.Value
import com.garan.tempo.DisplayUpdateMap
import com.garan.tempo.R

enum class DisplayMetric {
    ACTIVE_DURATION {
        override fun requiredDataType() = null
        override fun aggregationType() = AggregationType.NONE
        override fun displayNameId() = R.string.display_metric_active_duration
        override fun placeholder() = "8:88:88"
    },
    DISTANCE {
        override fun requiredDataType() = DataType.DISTANCE
        override fun aggregationType() = AggregationType.TOTAL
        override fun displayNameId() = R.string.display_metric_distance
        override fun placeholder() = "88.8"
    },
    SPEED {
        override fun requiredDataType() = DataType.SPEED
        override fun aggregationType() = AggregationType.SAMPLE
        override fun displayNameId() = R.string.display_metric_speed
        override fun placeholder() = "88.8"
    },
    AVG_SPEED {
        override fun requiredDataType() = DataType.SPEED
        override fun aggregationType() = AggregationType.AVG
        override fun displayNameId() = R.string.display_metric_avg_speed
        override fun placeholder() = "88.8"
    },
    CALORIES {
        override fun requiredDataType() = DataType.TOTAL_CALORIES
        override fun aggregationType() = AggregationType.TOTAL
        override fun displayNameId() = R.string.display_metric_calories
        override fun placeholder() = "8888"
    },
    PACE {
        override fun requiredDataType() = DataType.SPEED
        override fun aggregationType() = AggregationType.SAMPLE
        override fun displayNameId() = R.string.display_metric_pace
        override fun placeholder() = "88:88"
    },
    AVG_PACE {
        override fun requiredDataType() = DataType.SPEED
        override fun aggregationType() = AggregationType.AVG
        override fun displayNameId() = R.string.display_metric_avg_pace
        override fun placeholder() = "88:88"
    },
    CADENCE {
        override fun requiredDataType() = DataType.STEPS_PER_MINUTE
        override fun aggregationType() = AggregationType.SAMPLE
        override fun displayNameId() = R.string.display_metric_cadence
        override fun placeholder() = "888"
    },
    AVG_CADENCE {
        override fun requiredDataType() = DataType.STEPS_PER_MINUTE
        override fun aggregationType() = AggregationType.AVG
        override fun displayNameId() = R.string.display_metric_avg_cadence
        override fun placeholder() = "888"
    },
    HEART_RATE_BPM {
        override fun requiredDataType() = DataType.HEART_RATE_BPM
        override fun aggregationType() = AggregationType.SAMPLE
        override fun displayNameId() = R.string.display_metric_heart_rate_bpm
        override fun placeholder() = "888"
    },
    AVG_HEART_RATE {
        override fun requiredDataType() = DataType.HEART_RATE_BPM
        override fun aggregationType() = AggregationType.AVG
        override fun displayNameId() = R.string.display_metric_avg_heart_rate
        override fun placeholder() = "888"
    };

    abstract fun requiredDataType(): DataType?
    abstract fun aggregationType(): AggregationType
    abstract fun displayNameId(): Int
    abstract fun placeholder(): String
}

fun getSupportedDisplayMetrics(dataTypes: Set<DataType>) = DisplayMetric.values().filter {
    val requiredDataType = it.requiredDataType()
    requiredDataType == null || dataTypes.contains(requiredDataType)
}.toSet()

enum class AggregationType {
    NONE,
    MAX,
    MIN,
    TOTAL,
    AVG,
    SAMPLE
}

fun screenEditorDefaults(): DisplayUpdateMap = mutableStateMapOf(
    DisplayMetric.ACTIVE_DURATION to Value.ofLong(421),
    DisplayMetric.DISTANCE to Value.ofDouble(4124.0),
    DisplayMetric.SPEED to Value.ofDouble(2.5),
    DisplayMetric.AVG_SPEED to Value.ofDouble(2.7),
    DisplayMetric.CALORIES to Value.ofDouble(128.0),
    DisplayMetric.PACE to Value.ofDouble(2.7),
    DisplayMetric.AVG_PACE to Value.ofDouble(2.6),
    DisplayMetric.CADENCE to Value.ofLong(180),
    DisplayMetric.AVG_CADENCE to Value.ofLong(176),
    DisplayMetric.HEART_RATE_BPM to Value.ofDouble(121.0),
    DisplayMetric.AVG_HEART_RATE to Value.ofDouble(105.0)
)