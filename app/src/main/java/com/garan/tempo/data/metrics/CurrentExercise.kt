package com.garan.tempo.data.metrics

import com.garan.tempo.data.AvailabilityHolder
import java.time.ZonedDateTime
import java.util.EnumMap
import java.util.UUID

/**
 * Current exercise represents the state of the workout as passed to the UI from the service.
 */
data class CurrentExercise(
    val id: UUID = UUID.randomUUID(),
    val startDateTime: ZonedDateTime? = null,
    val availability: AvailabilityHolder = AvailabilityHolder.UNKNOWN,
    val metricsMap: EnumMap<TempoMetric, Number> = EnumMap<TempoMetric, Number>(TempoMetric::class.java),
    val metrics: Set<TempoMetric> = setOf()
) {
    val wasStarted: Boolean
        get() = startDateTime != null

    val requiresHeartRateIndicator = metrics.intersect(
        setOf(TempoMetric.HEART_RATE_BPM, TempoMetric.MAX_HEART_RATE, TempoMetric.AVG_HEART_RATE)
    ).isNotEmpty()
}