package com.garan.tempo.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.garan.tempo.data.metrics.TempoMetric
import java.time.Duration
import java.time.ZonedDateTime

data class SavedExerciseWithMetrics(
    @Embedded val savedExercise: SavedExercise = SavedExercise(),
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "exerciseId"
    )
    val savedExerciseMetrics: List<SavedExerciseMetric> = listOf()
)

/**
 * Represents the final summary of a workout.
 */
@Entity(tableName = "saved_exercises")
data class SavedExercise(
    @PrimaryKey
    var exerciseId: Long? = null,
    val recordingId: String = "",
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    val activeDuration: Duration = Duration.ZERO,
    val mapPathData: ByteArray? = null
)

/**
 * A summary metric saved at the end of the workout.
 */
@Entity(tableName = "saved_exercise_metrics")
data class SavedExerciseMetric(
    @PrimaryKey(autoGenerate = true)
    var savedExerciseMetricId: Int? = null,
    var exerciseId: Long? = null,
    val metric: TempoMetric,
    val doubleValue: Double?,
    val longValue: Long?
) {
    val value: Number
        get() = doubleValue ?: longValue!!
}