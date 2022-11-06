package com.garan.tempo.ui.screens.preworkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseType
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.garan.tempo.R
import com.garan.tempo.data.AvailabilityHolder
import com.garan.tempo.isInProgress
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.ui.components.GpsIndicator
import com.garan.tempo.ui.components.HrIndicator
import com.garan.tempo.ui.screens.workout.ServiceState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PreWorkoutPermissionCheck(
    onStartNavigate: () -> Unit,
    onStartExercise: () -> Unit,
    onPrepareExercise: () -> Unit,
    exerciseSettings: ExerciseSettingsWithScreens,
    serviceState: ServiceState
) {
    if (exerciseSettings.exerciseSettings.exerciseType != ExerciseType.UNKNOWN) {
        val permissionState = rememberMultiplePermissionsState(
            exerciseSettings.getRequiredPermissions().toList()
        )

        if (permissionState.allPermissionsGranted) {
            PreWorkoutScreen(
                onStartNavigate = onStartNavigate,
                serviceState = serviceState,
                onPrepareExercise = onPrepareExercise,
                onStartExercise = onStartExercise
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    text = stringResource(R.string.permissions_required),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        permissionState.launchMultiplePermissionRequest()
                    }) {
                    Text(stringResource(R.string.next))
                }
            }
        }
    }
}


@Composable
fun PreWorkoutScreen(
    onStartNavigate: () -> Unit,
    serviceState: ServiceState,
    onPrepareExercise: () -> Unit,
    onStartExercise: () -> Unit
) {
    if (serviceState is ServiceState.Connected) {
        val exerciseState by serviceState.exerciseState
        val availability by serviceState.availability
        PreWorkout(
            exerciseState = exerciseState,
            availability = availability,
            prepareExercise = onPrepareExercise,
            startExercise = onStartExercise,
            onStartNavigate = onStartNavigate
        )
    }
}

@Composable
fun PreWorkout(
    exerciseState: ExerciseState,
    availability: AvailabilityHolder,
    prepareExercise: () -> Unit,
    startExercise: () -> Unit,
    onStartNavigate: () -> Unit
) {
    var startButtonPressed by remember { mutableStateOf(false) }
    LaunchedEffect(exerciseState) {
        if (exerciseState.isInProgress) {
            onStartNavigate()
        } else if (exerciseState == ExerciseState.ENDED) {
            prepareExercise()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row {
            GpsIndicator(availability.locationAvailability)
            HrIndicator(availability.heartRateAvailability)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
            onClick = {
                if (!startButtonPressed) {
                    startButtonPressed = true
                    startExercise()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                stringResource(R.string.start)
            )
        }
    }
}