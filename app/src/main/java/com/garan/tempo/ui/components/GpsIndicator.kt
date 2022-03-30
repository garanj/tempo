package com.garan.tempo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.health.services.client.data.LocationAvailability
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.R

@Composable
fun GpsIndicator(locationAvailability: LocationAvailability) {
    when(locationAvailability) {
        LocationAvailability.ACQUIRED_TETHERED,
            LocationAvailability.ACQUIRED_UNTETHERED -> {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = stringResource(R.string.gps_fixed),
                    tint = MaterialTheme.colors.primary
                )
            }
        LocationAvailability.ACQUIRING -> {
            Icon(
                imageVector = Icons.Default.GpsNotFixed,
                contentDescription = stringResource(R.string.gps_acquiring),
                tint = MaterialTheme.colors.primaryVariant
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.GpsOff,
                contentDescription = stringResource(R.string.gps_not_available),
                tint = MaterialTheme.colors.secondary
            )
        }
    }
}