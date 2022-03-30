package com.garan.tempo.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.garan.tempo.R
import com.garan.tempo.TAG
import com.garan.tempo.settings.Units
import kotlinx.coroutines.launch

@Composable
fun UnitsToggle(
    units: Units,
    onCheckedChange: (Boolean) -> Unit
) {
    ToggleChip(
        checked = Units.IMPERIAL == units,
        onCheckedChange = onCheckedChange,
        toggleControl = { ToggleChipDefaults.SwitchIcon(Units.IMPERIAL == units) },
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