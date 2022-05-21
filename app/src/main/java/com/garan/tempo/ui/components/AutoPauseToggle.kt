package com.garan.tempo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
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
    val toggleChipColors = ToggleChipDefaults.toggleChipColors(
        checkedContentColor = MaterialTheme.colors.onSurface,
        uncheckedContentColor = MaterialTheme.colors.onSurface,
        checkedStartBackgroundColor = MaterialTheme.colors.surface,
        uncheckedStartBackgroundColor = MaterialTheme.colors.surface,
        checkedEndBackgroundColor = MaterialTheme.colors.surface,
        uncheckedEndBackgroundColor = MaterialTheme.colors.surface,
        checkedToggleControlColor = MaterialTheme.colors.primary,
        uncheckedToggleControlColor = MaterialTheme.colors.primary
        )
    ToggleChip(
        checked = isEnabled,
        colors = toggleChipColors,
        onCheckedChange = onCheckedChange,
        toggleControl = { Icon(
                imageVector = ToggleChipDefaults.switchIcon(isEnabled),
                contentDescription = ""
            )
        },
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