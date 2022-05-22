package com.garan.tempo.settings

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.garan.tempo.ui.metrics.AggregationType
import com.garan.tempo.ui.metrics.DisplayMetric

@Entity(tableName = "exercise_settings")
data class ExerciseSettings(
    @PrimaryKey(autoGenerate = true)
    var exerciseSettingsId: Long? = null,
    val name: String,
    val exerciseType: ExerciseType,
    val useAutoPause: Boolean,
    @Ignore
    var supportsAutoPause: Boolean = false,
    val recordingMetrics: Set<DataType> = setOf(),
) {
    constructor(
        name: String = "",
        exerciseType: ExerciseType = ExerciseType.UNKNOWN,
        useAutoPause: Boolean = false,
        recordingMetrics: Set<DataType> = setOf()
    ) : this(
        name = name,
        exerciseType = exerciseType,
        useAutoPause = useAutoPause,
        supportsAutoPause = false,
        recordingMetrics = recordingMetrics
    )

    fun getRequiresGps() = recordingMetrics.contains(DataType.LOCATION)
}

@Entity(tableName = "screen_settings")
data class ScreenSettings(
    @PrimaryKey(autoGenerate = true)
    var screenSettingsId: Int? = null,
    var screenIndex: Int = 0,
    var exerciseSettingsId: Long = 0,
    val screenFormat: ScreenFormat = ScreenFormat.SIX_SLOT,
    val metrics: List<DisplayMetric> = listOf()
)

data class ExerciseSettingsWithScreens(
    @Embedded val exerciseSettings: ExerciseSettings = ExerciseSettings(),
    @Relation(
        parentColumn = "exerciseSettingsId",
        entityColumn = "exerciseSettingsId"
    )
    val screenSettings: List<ScreenSettings> = listOf()
) {
    fun getDisplayMetricsSet() = screenSettings.flatMap { it.metrics }.toSet()

    fun getRequiredDataTypes(): Pair<Set<DataType>, Set<DataType>> {
        val dataTypes = exerciseSettings.recordingMetrics.toMutableSet()
        val aggregateDataTypes = mutableSetOf<DataType>()
        screenSettings.forEach {
            // Only take first n metrics based on the type of screen format
            // it is.
            it.metrics.subList(0, it.screenFormat.numSlots)
                .forEach {
                    when (it.aggregationType()) {
                        AggregationType.SAMPLE -> dataTypes.add(it.requiredDataType()!!)
                        AggregationType.AVG,
                        AggregationType.MIN,
                        AggregationType.MAX,
                        AggregationType.TOTAL -> aggregateDataTypes.add(it.requiredDataType()!!)
                        AggregationType.NONE -> {
                            // No aggregation for Active Duration DisplayMetric
                        }
                    }
                }
        }
        return dataTypes to aggregateDataTypes
    }
}

enum class ScreenFormat {
    ONE_SLOT(1),
    TWO_SLOT(2),
    ONE_PLUS_TWO_SLOT(3),
    ONE_PLUS_FOUR_SLOT(4),
    SIX_SLOT(6);

    var numSlots: Int = 0

    constructor()

    constructor(numSlots: Int) {
        this.numSlots = numSlots
    }
}