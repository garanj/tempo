package com.garan.tempo.ui.screens.screenformat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.settings.ScreenFormat

@Composable
fun ScreenFormatScreen(
    onScreenFormatClick: (ScreenFormat) -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        autoCentering = AutoCenteringParams(0, 100),
        anchorType = ScalingLazyListAnchorType.ItemStart
    ) {
        items(ScreenFormat.values().toList()) { screenFormat ->
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                onClick = {
                    onScreenFormatClick(screenFormat)
                },
                label = { Text(screenFormat.name) },
            )
        }
    }
}