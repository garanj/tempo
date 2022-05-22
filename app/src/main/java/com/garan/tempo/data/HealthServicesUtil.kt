package com.garan.tempo.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Sports
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.health.services.client.data.ExerciseType

val ExerciseType.imageVector: ImageVector
    get() = when (this) {
        ExerciseType.RUNNING -> Icons.Default.DirectionsRun
        ExerciseType.BIKING -> Icons.Default.DirectionsBike
        ExerciseType.WALKING -> Icons.Default.DirectionsWalk
        else -> Icons.Default.Sports
    }