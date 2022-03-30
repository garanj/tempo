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
import androidx.wear.compose.material.Text
import com.garan.tempo.TempoService
import com.garan.tempo.R
import com.garan.tempo.Screen
import com.garan.tempo.TAG
import com.garan.tempo.UiState
import com.garan.tempo.isInProgress
import com.garan.tempo.rememberUiState

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun PostWorkoutScreen(
    uiState: UiState,
    service: TempoService?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Finished!")
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
fun PostWorkoutScreenPreview() {
    val uiState = rememberUiState()
    PostWorkoutScreen(
        uiState = uiState,
        service = null
    )
}
