package com.garan.tempo.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.garan.tempo.TempoService
import com.garan.tempo.Screen
import com.garan.tempo.TAG
import com.garan.tempo.UiState
import com.garan.tempo.isInProgress
import com.garan.tempo.ui.components.GpsIndicator
import com.garan.tempo.ui.components.HrIndicator

/**
 * Composable functions for use when connected to the fan, either when in HR-guided or non-HR mode.
 */

@Composable
fun PreWorkoutScreen(
    service: TempoService,
    uiState: UiState,
    screenStarted: Boolean = uiState.navHostController
        .getBackStackEntry(Screen.PRE_WORKOUT.route)
        .lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED),
    onClick: () -> Unit,
    onSwipeBack: () -> Unit
) {
    val exerciseState by service.exerciseState
    val locationAvailability by service.locationAvailability
    val hrAvailability by service.hrAvailability

    // Hold state reflecting how this screen is being closed.
    val closedBySwipe = remember { mutableStateOf(true) }

    LaunchedEffect(screenStarted, exerciseState) {
        if (screenStarted) {
            if (exerciseState.isInProgress) {
                closedBySwipe.value = false
                uiState.navHostController.popBackStack(Screen.START_MENU.route, true)
                uiState.navHostController.navigate(Screen.WORKOUT.route)
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row {
            GpsIndicator(locationAvailability = locationAvailability)
            HrIndicator(dataTypeAvailability = hrAvailability)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                "Start"
            )
        }
    }

    // Side effect used to disconnect from the fan when dismissing the connected screen.
    DisposableEffect(Unit) {
        onDispose {
            // Only call the action associated with swiping back (i.e. instructing Service to
            // disconnect) if this clean up is happening from a swipe.
            if (closedBySwipe.value) {
                onSwipeBack.invoke()
            }
        }
    }
}
