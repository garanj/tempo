package com.garan.tempo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.LocationAvailability
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.R

@Composable
fun HrIndicator(dataTypeAvailability: DataTypeAvailability) {
    when(dataTypeAvailability) {
        DataTypeAvailability.AVAILABLE -> {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.hr_acquired),
                    tint = MaterialTheme.colors.primary
                )
            }
        DataTypeAvailability.ACQUIRING -> {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = stringResource(R.string.hr_acquiring),
                tint = MaterialTheme.colors.primaryVariant
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = stringResource(R.string.hr_not_available),
                tint = MaterialTheme.colors.secondary
            )
        }
    }
}