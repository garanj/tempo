package com.garan.tempo.data.metrics

import androidx.health.services.client.data.DataType
import com.garan.tempo.R
import java.util.EnumMap

/**
 * Represents a metric used in Tempo, either for display during the workout, or recording.
 *
 * Closely aligned with DataTypes in Health Services, but not the same: Includes formatting
 * functionality, and specification of exact metric (e.g. Max or avg) where statistical data types
 * are used.
 */
enum class TempoMetric {
    ACTIVE_DURATION {
        override val requiredDataType = null
        override val displayNameId = R.string.display_metric_active_duration
        override val placeholder = "8:88:88"
        override val aggregationType = AggregationType.NONE
        override val screenEditorDefault = 421
    },
    DISTANCE {
        override val requiredDataType = DataType.DISTANCE_TOTAL
        override val displayNameId = R.string.display_metric_distance
        override val placeholder = "88.88"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 4124.0
    },
    SPEED {
        override val requiredDataType = DataType.SPEED
        override val displayNameId = R.string.display_metric_speed
        override val placeholder = "88.8"
        override val aggregationType = AggregationType.SAMPLE
        override val screenEditorDefault = 2.5
    },
    AVG_SPEED {
        override val requiredDataType = DataType.SPEED_STATS
        override val displayNameId = R.string.display_metric_avg_speed
        override val placeholder = "88.8"
        override val aggregationType = AggregationType.AVG
        override val screenEditorDefault = 2.7
    },
    MAX_SPEED {
        override val requiredDataType = DataType.SPEED_STATS
        override val displayNameId = R.string.display_metric_max_speed
        override val placeholder = "88.8"
        override val aggregationType = AggregationType.MAX
        override val screenEditorDefault = 3.1
    },
    CALORIES {
        override val requiredDataType = DataType.CALORIES_TOTAL
        override val displayNameId = R.string.display_metric_calories
        override val placeholder = "8888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 128.0
    },
    PACE {
        override val requiredDataType = DataType.PACE
        override val displayNameId = R.string.display_metric_pace
        override val placeholder = "88:88"
        override val aggregationType = AggregationType.SAMPLE
        override val screenEditorDefault = 2.7
    },
    AVG_PACE {
        override val requiredDataType = DataType.PACE_STATS
        override val displayNameId = R.string.display_metric_avg_pace
        override val placeholder = "88:88"
        override val aggregationType = AggregationType.AVG
        override val screenEditorDefault = 2.6
    },
    MAX_PACE {
        override val requiredDataType = DataType.PACE_STATS
        override val displayNameId = R.string.display_metric_max_pace
        override val placeholder = "88:88"
        override val aggregationType = AggregationType.MAX
        override val screenEditorDefault = 2.9
    },
    CADENCE {
        override val requiredDataType = DataType.STEPS_PER_MINUTE
        override val displayNameId = R.string.display_metric_cadence
        override val placeholder = "888"
        override val aggregationType = AggregationType.SAMPLE
        override val screenEditorDefault = 180
    },
    AVG_CADENCE {
        override val requiredDataType = DataType.STEPS_PER_MINUTE_STATS
        override val displayNameId = R.string.display_metric_avg_cadence
        override val placeholder = "888"
        override val aggregationType = AggregationType.AVG
        override val screenEditorDefault = 176
    },
    MAX_CADENCE {
        override val requiredDataType = DataType.STEPS_PER_MINUTE_STATS
        override val displayNameId = R.string.display_metric_max_cadence
        override val placeholder = "888"
        override val aggregationType = AggregationType.MAX
        override val screenEditorDefault = 192
    },
    HEART_RATE_BPM {
        override val requiredDataType = DataType.HEART_RATE_BPM
        override val displayNameId = R.string.display_metric_heart_rate_bpm
        override val placeholder = "888"
        override val aggregationType = AggregationType.SAMPLE
        override val screenEditorDefault = 121.0
    },
    AVG_HEART_RATE {
        override val requiredDataType = DataType.HEART_RATE_BPM_STATS
        override val displayNameId = R.string.display_metric_avg_heart_rate
        override val placeholder = "888"
        override val aggregationType = AggregationType.AVG
        override val screenEditorDefault = 105.0
    },
    MAX_HEART_RATE {
        override val requiredDataType = DataType.HEART_RATE_BPM_STATS
        override val displayNameId = R.string.display_metric_max_heart_rate
        override val placeholder = "888"
        override val aggregationType = AggregationType.MAX
        override val screenEditorDefault = 166.0
    },
    TOTAL_STEPS {
        override val requiredDataType = DataType.STEPS_TOTAL
        override val displayNameId = R.string.display_metric_total_steps
        override val placeholder = "88888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 5627
    },
    DECLINE_DURATION {
        override val requiredDataType = DataType.DECLINE_DURATION_TOTAL
        override val displayNameId = R.string.display_metric_decline_duration
        override val placeholder = "8:88:88"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 512.0
    },
    INCLINE_DURATION {
        override val requiredDataType = DataType.INCLINE_DURATION_TOTAL
        override val displayNameId = R.string.display_metric_incline_duration
        override val placeholder = "88:88"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 214.2
    },
    RESTING_DURATION {
        override val requiredDataType = DataType.RESTING_EXERCISE_DURATION_TOTAL
        override val displayNameId = R.string.display_metric_resting_duration
        override val placeholder = "88:88"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 33.2
    },
    MAX_ELEVATION {
        override val requiredDataType = DataType.ABSOLUTE_ELEVATION_STATS
        override val displayNameId = R.string.display_metric_max_elevation
        override val placeholder = "88:88"
        override val aggregationType = AggregationType.MAX
        override val screenEditorDefault = 421.2
    },
    DECLINE_DISTANCE {
        override val requiredDataType = DataType.DECLINE_DISTANCE_TOTAL
        override val displayNameId = R.string.display_metric_decline_distance
        override val placeholder = "88.88"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 8530.2
    },
    ELEVATION_GAIN {
        override val requiredDataType = DataType.ELEVATION_GAIN_TOTAL
        override val displayNameId = R.string.display_metric_elevation_gain
        override val placeholder = "8888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 221.0
    },
    ELEVATION_LOSS {
        override val requiredDataType = DataType.ELEVATION_LOSS_TOTAL
        override val displayNameId = R.string.display_metric_elevation_loss
        override val placeholder = "8888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 240.1
    },
    FLAT_GROUND_DISTANCE {
        override val requiredDataType = DataType.FLAT_GROUND_DISTANCE_TOTAL
        override val displayNameId = R.string.display_metric_flat_ground_distance
        override val placeholder = "88.88"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 3152.0
    },
    FLOORS {
        override val requiredDataType = DataType.FLOORS_TOTAL
        override val displayNameId = R.string.display_metric_floors
        override val placeholder = "888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 12
    },
    GOLF_SHOT_TOTAL {
        override val requiredDataType = DataType.GOLF_SHOT_COUNT_TOTAL
        override val displayNameId = R.string.display_metric_golf_shot_total
        override val placeholder = "888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 72
    },
    INCLINE_DISTANCE {
        override val requiredDataType = DataType.INCLINE_DISTANCE_TOTAL
        override val displayNameId = R.string.display_metric_incline_distance
        override val placeholder = "88.88"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 1312.3
    },
    REP_COUNT {
        override val requiredDataType = DataType.REP_COUNT_TOTAL
        override val displayNameId = R.string.display_metric_rep_count
        override val placeholder = "88"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 25
    },
    RUNNING_STEPS {
        override val requiredDataType = DataType.RUNNING_STEPS_TOTAL
        override val displayNameId = R.string.display_metric_running_steps
        override val placeholder = "88888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 11020
    },
    SWIMMING_LAPS {
        override val requiredDataType = DataType.SWIMMING_LAP_COUNT
        override val displayNameId = R.string.display_metric_swimming_laps
        override val placeholder = "888"
        override val aggregationType = AggregationType.SAMPLE
        override val screenEditorDefault = 20
    },
    SWIMMING_STROKES {
        override val requiredDataType = DataType.SWIMMING_STROKES_TOTAL
        override val displayNameId = R.string.display_metric_swimming_strokes
        override val placeholder = "88888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 221
    },
    WALKING_STEPS {
        override val requiredDataType = DataType.WALKING_STEPS_TOTAL
        override val displayNameId = R.string.display_metric_walking_steps
        override val placeholder = "88888"
        override val aggregationType = AggregationType.TOTAL
        override val screenEditorDefault = 8127
    };

    enum class AggregationType {
        NONE,
        SAMPLE,
        INTERVAL,
        MAX,
        MIN,
        AVG,
        TOTAL
    }

    abstract val requiredDataType: DataType<*, *>?
    abstract val placeholder: String
    abstract val displayNameId: Int
    abstract val aggregationType: AggregationType
    abstract val screenEditorDefault: Number

    companion object {
        val screenEditorDefaults by lazy {
            EnumMap<TempoMetric, Number>(TempoMetric::class.java).apply {
                putAll(values().associateWith {
                    it.screenEditorDefault
                })
            }
        }

        /**
         * For a given set of [DataType]s, returns the [TempoMetric]s that are supported from these
         * datatypes.
         */
        fun getSupportedTempoMetrics(dataTypes: Set<DataType<*, *>>) = TempoMetric.values().filter {
            val requiredDataType = it.requiredDataType
            requiredDataType == null || dataTypes.contains(requiredDataType)
        }.toSet()
    }
}