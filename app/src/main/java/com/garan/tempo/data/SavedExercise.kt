package com.garan.tempo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Represents the final summary of a workout.
 */
@Entity(tableName = "saved_exercises")
data class SavedExercise(
    @PrimaryKey
    val exerciseId: String = "",
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    val totalDistance: Double? = null,
    val totalCalories: Double? = null,
    val avgPace: Double? = null,
    val avgHeartRate: Double? = null,
    val activeDuration: Duration? = null,
    val hasMap: Boolean = false
)