package com.garan.tempo.ui.screens.otherapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Text
import com.garan.tempo.R

/**
 * Placeholder implementation for when an existing Health Services exercise is in progress, but in
 * another app. Potentially expand to offer the user the option to proceed with Tempo, cancelling
 * the existing workout.
 */
@Composable
fun OtherAppInProgressScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.other_app_in_progress_message),
            textAlign = TextAlign.Center
        )
    }
}