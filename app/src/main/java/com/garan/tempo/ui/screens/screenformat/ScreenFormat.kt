package com.garan.tempo.ui.screens.screenformat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.settings.ScreenFormat
import kotlinx.coroutines.launch

@Composable
fun ScreenFormatScreen(
    onScreenFormatClick: suspend (ScreenFormat) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        autoCentering = AutoCenteringParams(),
        anchorType = ScalingLazyListAnchorType.ItemStart
    ) {
        items(ScreenFormat.values().toList()) { screenFormat ->
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                onClick = {
                    coroutineScope.launch {
                        onScreenFormatClick(screenFormat)
                    }
                },
                label = { Text(screenFormat.name) },
            )
        }
    }
}