package com.garan.tempo.data

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.Value
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.garan.tempo.ui.metrics.DisplayMetric
import java.time.Duration
import java.time.ZonedDateTime

@Entity(tableName = "saved_exercises")
data class SavedExercise (
    @PrimaryKey
    val exerciseId: String = "",
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    val totalDistance: Double? = null,
    val totalCalories: Double? = null,
    val avgPace: Double? = null,
    val avgHeartRate: Double? = null,
    val activeDuration: Duration? = null
)

@Entity(tableName = "exercise_metric_cache")
data class SavedExerciseMetricCache(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val datetime: Long,
    val lat: Double? = null,
    val lng: Double? = null,
    val elevation: Double? = null,
    val hr: Double? = null,
    val stepsPerMin: Double? = null
)