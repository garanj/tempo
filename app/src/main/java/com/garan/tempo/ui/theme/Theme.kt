package com.garan.tempo.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun TempoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Colors,
        content = content
    )
}
