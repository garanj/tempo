package com.garan.tempo.ui.screens.screenformat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.settings.ScreenFormat
import com.garan.tempo.ui.screens.workout.ActiveScreen
import com.garan.tempo.ui.theme.TempoTheme
import kotlinx.coroutines.launch

@Composable
fun ScreenFormatScreen(
    onScreenFormatClick: suspend (ScreenFormat) -> Unit = {}
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
                label = {
                    val label = stringResource(id = screenFormat.labelId)
                    Text(label)
                },
            )
        }
    }
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun ScreenFormatPreview() {
    TempoTheme {
        ScreenFormatScreen()
    }
}

