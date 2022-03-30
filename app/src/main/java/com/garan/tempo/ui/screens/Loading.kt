package com.garan.tempo.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.ExerciseState
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.garan.tempo.TempoService
import com.garan.tempo.R
import com.garan.tempo.Screen
import com.garan.tempo.TAG
import com.garan.tempo.UiState
import com.garan.tempo.isInProgress
import com.garan.tempo.rememberUiState

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun TempoLoadingMessage(
    uiState: UiState,
    service: TempoService?,
    exerciseState: ExerciseState? = service?.exerciseState?.value
) {
    LaunchedEffect(service) {
        service?.let {
            uiState.navHostController.popBackStack(Screen.LOADING.route, true)
            val dest = if (it.exerciseState.value.isInProgress) {
                Screen.WORKOUT.route
            } else {
                Screen.START_MENU.route
            }
            Log.i(TAG, "Navigating to $dest ${it.exerciseState.value}")
            uiState.navHostController.navigate(dest)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        Image(
            painter = painterResource(
                id = R.drawable.ic_launcher_foreground
            ),
            contentDescription = context.getString(R.string.loading_message)
        )
    }
}

@Preview(
    widthDp = WEAR_PREVIEW_DEVICE_WIDTH_DP,
    heightDp = WEAR_PREVIEW_DEVICE_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun LoadingScreenPreview() {
    val uiState = rememberUiState()
    TempoLoadingMessage(
        uiState = uiState,
        service = null,
        exerciseState = ExerciseState.USER_ENDED
    )
}
