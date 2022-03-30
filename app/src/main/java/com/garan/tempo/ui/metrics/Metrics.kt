package com.garan.tempo.ui.metrics

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.Value
import com.garan.tempo.DisplayUpdateMap
import com.garan.tempo.R
import kotlinx.serialization.Serializable

@Serializable
enum class DisplayMetric {
    ACTIVE_DURATION {
        override fun requiredDataType() = null
        override fun aggregationType() = AggregationType.NONE
    },
    DISTANCE {
        override fun requiredDataType() = DataType.DISTANCE
        override fun aggregationType() = AggregationType.TOTAL
    },
    SPEED {
        override fun requiredDataType() = DataType.SPEED
        override fun aggregationType() = AggregationType.SAMPLE
    },
    AVG_SPEED {
        override fun requiredDataType() = DataType.SPEED
        override fun aggregationType() = AggregationType.AVG
    },
    CALORIES {
        override fun requiredDataType() = DataType.TOTAL_CALORIES
        override fun aggregationType() = AggregationType.TOTAL
    },
    PACE {
        override fun requiredDataType() = DataType.SPEED
        override fun aggregationType() = AggregationType.SAMPLE
    },
    AVG_PACE {
        override fun requiredDataType() = DataType.SPEED
        override fun aggregationType() = AggregationType.AVG
    },
    CADENCE {
        override fun requiredDataType() = DataType.STEPS_PER_MINUTE
        override fun aggregationType() = AggregationType.SAMPLE
    },
    AVG_CADENCE {
        override fun requiredDataType() = DataType.STEPS_PER_MINUTE
        override fun aggregationType() = AggregationType.AVG
    },
    HEART_RATE_BPM {
        override fun requiredDataType() = DataType.HEART_RATE_BPM
        override fun aggregationType() = AggregationType.SAMPLE
    },
    AVG_HEART_RATE {
        override fun requiredDataType() = DataType.HEART_RATE_BPM
        override fun aggregationType() = AggregationType.AVG
    };

    abstract fun requiredDataType(): DataType?
    abstract fun aggregationType(): AggregationType
}

enum class AggregationType {
    NONE,
    MAX,
    MIN,
    TOTAL,
    AVG,
    SAMPLE
}

fun screenEditorDefaults() : DisplayUpdateMap = mutableStateMapOf(
    DisplayMetric.ACTIVE_DURATION to Value.ofLong(421),
    DisplayMetric.DISTANCE to Value.ofDouble(4124.0),
    DisplayMetric.SPEED to Value.ofDouble(2.5),
    DisplayMetric.AVG_SPEED to Value.ofDouble(2.7),
    DisplayMetric.CALORIES to Value.ofDouble(128.0),
    DisplayMetric.PACE to Value.ofDouble(1 / 2.5),
    DisplayMetric.AVG_PACE to Value.ofDouble(1 / 2.7),
    DisplayMetric.CADENCE to Value.ofLong(180),
    DisplayMetric.AVG_CADENCE to Value.ofLong(176),
    DisplayMetric.HEART_RATE_BPM to Value.ofDouble(121.0),
    DisplayMetric.AVG_HEART_RATE to Value.ofDouble(105.0)
)