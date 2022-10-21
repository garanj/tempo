package com.garan.tempo.settings

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.garan.tempo.data.requiredPermissions
import com.garan.tempo.ui.metrics.TempoMetric

@Entity(tableName = "exercise_settings")
data class ExerciseSettings(
    @PrimaryKey(autoGenerate = true)
    var exerciseSettingsId: Long? = null,
    val name: String,
    val exerciseType: ExerciseType,
    val useAutoPause: Boolean,
    @Ignore
    var supportsAutoPause: Boolean = false,
    val recordingMetrics: Set<DataType<*, *>> = setOf(),
    val endSummaryMetrics: List<TempoMetric> = listOf()
) {
    constructor(
        name: String = "",
        exerciseType: ExerciseType = ExerciseType.UNKNOWN,
        useAutoPause: Boolean = false,
        recordingMetrics: Set<DataType<*, *>> = setOf(),
        endSummaryMetrics: List<TempoMetric> = listOf()
    ) : this(
        name = name,
        exerciseType = exerciseType,
        useAutoPause = useAutoPause,
        supportsAutoPause = false,
        recordingMetrics = recordingMetrics,
        endSummaryMetrics = endSummaryMetrics
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
    val metrics: List<TempoMetric> = listOf()
)

data class ExerciseSettingsWithScreens(
    @Embedded val exerciseSettings: ExerciseSettings = ExerciseSettings(),
    @Relation(
        parentColumn = "exerciseSettingsId",
        entityColumn = "exerciseSettingsId"
    )
    val screenSettings: List<ScreenSettings> = listOf()
) {
    @Ignore
    val displayMetricsSet = screenSettings.flatMap { it.metrics }.toSet()

    fun getRequiredDataTypes(): Set<DataType<*, *>> {
        // Start with the set of metrics required for recording (i.e. those that will be written to
        // database, but aren't for UI display necessarily.
        val dataTypes = exerciseSettings.recordingMetrics.toMutableSet()
        val aggregateDataTypes = mutableSetOf<DataType<*, *>>()
        //
        screenSettings.forEach { screenSetting ->
            // Only take first n metrics based on the type of screen format
            // it is.
            screenSetting.metrics.subList(0, screenSetting.screenFormat.numSlots)
                .forEach { displayMetric ->
                    displayMetric.requiredDataType?.let {
                        dataTypes.add(it)
                    }
                }
        }
        // Ensure data types for the metrics to be shown in the workout summary post-workout are
        // added.
        exerciseSettings.endSummaryMetrics
            .forEach { displayMetric ->
                displayMetric.requiredDataType?.let { dataTypes.add(it) }
            }
        return dataTypes
    }

    fun getRequiredPermissions() = getRequiredDataTypes().map { dataType ->
        dataType.requiredPermissions
    }.flatten().toSet()
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