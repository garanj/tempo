package com.garan.tempo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.garan.tempo.R
import com.garan.tempo.settings.Units

@Composable
fun UnitsToggle(
    units: Units,
    onCheckedChange: (Boolean) -> Unit
) {
    ToggleChip(
        checked = Units.IMPERIAL == units,
        onCheckedChange = onCheckedChange,
        toggleControl = { ToggleChipDefaults.switchIcon(Units.IMPERIAL == units) },
        label = {
            val id = when(units) {
                Units.IMPERIAL -> R.string.imperial_label
                else -> R.string.metric_label
            }
            Text(stringResource(id = id))
        },
        secondaryLabel = { Text(stringResource(id = R.string.units_label)) }
    )
}