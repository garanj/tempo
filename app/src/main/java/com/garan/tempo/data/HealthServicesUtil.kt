package com.garan.tempo.data

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Sports
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseType
import kotlin.reflect.KClass

// Icons for various exercise types
// TODO - make all exercise types work
val ExerciseType.imageVector: ImageVector
    get() = when (this) {
        ExerciseType.RUNNING -> Icons.Default.DirectionsRun
        ExerciseType.BIKING -> Icons.Default.DirectionsBike
        ExerciseType.WALKING -> Icons.Default.DirectionsWalk
        else -> Icons.Default.Sports
    }

// Extension to obtain the set of permissions required for a given [DataType].
val DataType<*, *>.requiredPermissions: Set<String>
    get() = mutableSetOf<String>().also {
        if (ACTIVITY_RECOGNITION_SET.contains(this)) {
            it.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (BODY_SENSOR_SET.contains(this)) {
            it.add(Manifest.permission.BODY_SENSORS)
        }
        if (ACCESS_FINE_LOCATION_SET.contains(this)) {
            it.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }.toSet()

// TODO add requires GPS to data type.

data class DataTypeMetadata(
    val dataType: DataType<*, *>,
    val requiresActivityRecognition: Boolean = false,
    val requiresAccessFineLocation: Boolean = false,
    val requiresBodySensors: Boolean = false,
    val valueType: KClass<*>
)

val dataTypeDetails = setOf(
    DataTypeMetadata(
        dataType = DataType.HEART_RATE_BPM,
        requiresBodySensors = true,
        valueType = Double::class
    )
)

val NO_PERMISSION_SET = setOf(
    DataType.ACTIVE_EXERCISE_DURATION_TOTAL,
    DataType.DECLINE_DURATION_TOTAL,
    DataType.DECLINE_DURATION,
    DataType.FLAT_GROUND_DURATION_TOTAL,
    DataType.FLAT_GROUND_DURATION,
    DataType.INCLINE_DURATION_TOTAL,
    DataType.INCLINE_DURATION,
    DataType.RESTING_EXERCISE_DURATION_TOTAL,
    DataType.RESTING_EXERCISE_DURATION
)

val ACTIVITY_RECOGNITION_SET = setOf(
    DataType.ABSOLUTE_ELEVATION_STATS,
    DataType.ABSOLUTE_ELEVATION,
    DataType.CALORIES_DAILY,
    DataType.CALORIES_TOTAL,
    DataType.CALORIES,
    DataType.DECLINE_DISTANCE_TOTAL,
    DataType.DECLINE_DISTANCE,
    DataType.DISTANCE_DAILY,
    DataType.DISTANCE_TOTAL,
    DataType.DISTANCE,
    DataType.ELEVATION_GAIN_TOTAL,
    DataType.ELEVATION_GAIN,
    DataType.ELEVATION_LOSS_TOTAL,
    DataType.ELEVATION_LOSS,
    DataType.FLAT_GROUND_DISTANCE_TOTAL,
    DataType.FLAT_GROUND_DISTANCE,
    DataType.FLOORS_DAILY,
    DataType.FLOORS_TOTAL,
    DataType.FLOORS,
    DataType.GOLF_SHOT_COUNT_TOTAL,
    DataType.GOLF_SHOT_COUNT,
    DataType.INCLINE_DISTANCE_TOTAL,
    DataType.INCLINE_DISTANCE,
    DataType.PACE_STATS,
    DataType.PACE,
    DataType.REP_COUNT_TOTAL,
    DataType.REP_COUNT,
    DataType.RUNNING_STEPS_TOTAL,
    DataType.RUNNING_STEPS,
    DataType.SPEED_STATS,
    DataType.SPEED,
    DataType.STEPS_DAILY,
    DataType.STEPS_PER_MINUTE_STATS,
    DataType.STEPS_PER_MINUTE,
    DataType.STEPS_TOTAL,
    DataType.STEPS,
    DataType.SWIMMING_LAP_COUNT,
    DataType.SWIMMING_STROKES_TOTAL,
    DataType.SWIMMING_STROKES,
    DataType.WALKING_STEPS_TOTAL,
    DataType.WALKING_STEPS
)

val BODY_SENSOR_SET = setOf(
    DataType.HEART_RATE_BPM,
    DataType.HEART_RATE_BPM_STATS,
    DataType.VO2_MAX,
    DataType.VO2_MAX_STATS
)

val ACCESS_FINE_LOCATION_SET = setOf(DataType.LOCATION)

val dataTypes = ACTIVITY_RECOGNITION_SET + BODY_SENSOR_SET + ACCESS_FINE_LOCATION_SET

fun ExerciseState.isAutoPauseState() = setOf(
    ExerciseState.AUTO_PAUSED,
    ExerciseState.AUTO_PAUSING,
    ExerciseState.AUTO_RESUMING
).contains(this)