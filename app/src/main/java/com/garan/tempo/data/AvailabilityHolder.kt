package com.garan.tempo.data

import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.LocationAvailability

data class AvailabilityHolder(
    val heartRateAvailability: DataTypeAvailability = DataTypeAvailability.UNKNOWN,
    val locationAvailability: LocationAvailability = LocationAvailability.UNKNOWN
) {
    companion object {
        val ALL_AVAILABLE = AvailabilityHolder(
            heartRateAvailability = DataTypeAvailability.AVAILABLE,
            locationAvailability = LocationAvailability.ACQUIRED_UNTETHERED
        )
        val UNKNOWN = AvailabilityHolder(
            heartRateAvailability = DataTypeAvailability.UNKNOWN,
            locationAvailability = LocationAvailability.UNKNOWN
        )
    }
}