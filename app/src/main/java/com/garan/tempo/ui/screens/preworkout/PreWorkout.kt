package com.garan.tempo.ui.screens.preworkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.LocationAvailability
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.garan.tempo.ui.components.GpsIndicator
import com.garan.tempo.ui.components.HrIndicator
import com.garan.tempo.ui.screens.workout.ServiceState

@Composable
fun PreWorkoutScreen(
    onStartNavigate: () -> Unit,
    viewModel: PreWorkoutViewModel = hiltViewModel<PreWorkoutViewModel>()
) {
    val serviceState by viewModel.serviceState

    if (serviceState is ServiceState.Connected) {
        val service = serviceState as ServiceState.Connected
        val exerciseState by service.exerciseState
        val locationAvailability by service.locationAvailability
        val hrAvailability by service.hrAvailability
        PreWorkout(
            exerciseState = exerciseState,
            locationAvailability = locationAvailability,
            hrAvailability = hrAvailability,
            prepareExercise = {
                viewModel.prepare()
            },
            startExercise = {
                viewModel.startExercise()
            },
            onStartNavigate = onStartNavigate
        )
    }
}

@Composable
fun PreWorkout(
    exerciseState: ExerciseState,
    locationAvailability: LocationAvailability,
    hrAvailability: DataTypeAvailability,
    prepareExercise: () -> Unit,
    startExercise: () -> Unit,
    onStartNavigate: () -> Unit
) {
    LaunchedEffect(exerciseState) {
        if (exerciseState == ExerciseState.ACTIVE) {
            onStartNavigate()
        } else if (exerciseState == ExerciseState.USER_ENDED) {
            prepareExercise()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row {
            GpsIndicator(locationAvailability)
            HrIndicator(hrAvailability)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
            onClick = startExercise
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                // TODO
                "Start"
            )
        }
    }
}