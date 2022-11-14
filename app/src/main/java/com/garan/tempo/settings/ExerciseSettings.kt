package com.garan.tempo.settings

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.garan.tempo.R
import com.garan.tempo.data.metrics.TempoMetric
import com.garan.tempo.data.requiredPermissions

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
    val endSummaryMetrics: Set<TempoMetric> = setOf()
) {
    constructor(
        name: String = "",
        exerciseType: ExerciseType = ExerciseType.UNKNOWN,
        useAutoPause: Boolean = false,
        recordingMetrics: Set<DataType<*, *>> = setOf(),
        endSummaryMetrics: Set<TempoMetric> = setOf()
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