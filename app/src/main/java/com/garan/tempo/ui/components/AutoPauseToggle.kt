package com.garan.tempo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.garan.tempo.R
import com.garan.tempo.ui.theme.TempoTheme

@Composable
fun AutoPauseToggle(
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ToggleChip(
        checked = isEnabled,
        onCheckedChange = onCheckedChange,
        toggleControl = { ToggleChipDefaults.SwitchIcon(isEnabled) },
        label = {
            Text(stringResource(id = R.string.auto_pause_label))
        }
    )
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun AutoPauseTogglePreview() {
    TempoTheme {
        AutoPauseToggle(
            isEnabled = true,
            onCheckedChange = {}
        )
    }
}