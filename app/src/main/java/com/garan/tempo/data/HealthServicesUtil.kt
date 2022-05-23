package com.garan.tempo.data

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Sports
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType


val ExerciseType.imageVector: ImageVector
    get() = when (this) {
        ExerciseType.RUNNING -> Icons.Default.DirectionsRun
        ExerciseType.BIKING -> Icons.Default.DirectionsBike
        ExerciseType.WALKING -> Icons.Default.DirectionsWalk
        else -> Icons.Default.Sports
    }

val DataType.requiredPermissions: Set<String>
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

val ACTIVITY_RECOGNITION_SET = setOf(
    DataType.ABSOLUTE_ELEVATION,
    DataType.DAILY_CALORIES,
    DataType.DAILY_DISTANCE,
    DataType.DAILY_FLOORS,
    DataType.DAILY_STEPS,
    DataType.DECLINE_DISTANCE,
    DataType.DISTANCE,
    DataType.ELEVATION_GAIN,
    DataType.FLAT_GROUND_DISTANCE,
    DataType.FLOORS,
    DataType.INCLINE_DISTANCE,
    DataType.PACE,
    DataType.REP_COUNT,
    DataType.RUNNING_STEPS,
    DataType.SPEED,
    DataType.STEPS_PER_MINUTE,
    DataType.STEPS,
    DataType.SWIMMING_LAP_COUNT,
    DataType.SWIMMING_STROKES,
    DataType.TOTAL_CALORIES,
    DataType.WALKING_STEPS,
)

val BODY_SENSOR_SET = setOf(
    DataType.HEART_RATE_BPM,
    DataType.SPO2,
    DataType.VO2,
    DataType.VO2_MAX
)

val ACCESS_FINE_LOCATION_SET = setOf(DataType.LOCATION)