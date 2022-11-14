package com.garan.tempo.data.metrics

import androidx.compose.runtime.referentialEqualityPolicy
import androidx.health.services.client.data.AggregateDataType
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.CumulativeDataPoint
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.IntervalDataPoint
import androidx.health.services.client.data.LocationAvailability
import androidx.health.services.client.data.SampleDataPoint
import androidx.health.services.client.data.StatisticalDataPoint
import com.garan.tempo.ExerciseMessage
import com.garan.tempo.data.AvailabilityHolder
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.EnumMap
import java.util.UUID

/**
 * Repository used by the ExerciseService for maintaining the state of metrics, given updates from
 * Health Services.
 *
 * At present, a new [CurrentExercise] is created when the metrics/availability change, although the
 * underlying enum map remains the same. So this is used with a [referentialEqualityPolicy] when
 * providing state updates in the Service.
 */
class CurrentExerciseRepository {
    private var currentExercise: CurrentExercise? = null

    // The latest snapshot of metrics, for visualisation in the UI.
    private val metricsMap = EnumMap<TempoMetric, Number>(TempoMetric::class.java)

    // Used to keep track of the state of the HR sensor.
    private var dataAvailability = AvailabilityHolder.UNKNOWN

    private var startDateTime: ZonedDateTime? = null

    fun initializeCurrentExercise(metrics: Set<TempoMetric> = setOf()) {
        currentExercise = CurrentExercise(
            id = UUID.randomUUID(),
            availability = AvailabilityHolder.UNKNOWN,
            metricsMap = metricsMap,
            metrics = metrics
        )
    }

    fun disposeCurrentExercise() {
        currentExercise = null
        metricsMap.clear()
        startDateTime = null
        dataAvailability = AvailabilityHolder.UNKNOWN
    }

    fun hasCurrentExercise() = currentExercise != null

    fun updateFromExerciseMessage(exerciseMessage: ExerciseMessage): CurrentExercise {
        check(currentExercise != null)
        when (exerciseMessage) {
            is ExerciseMessage.ExerciseUpdateMessage -> {
                mergeExerciseUpdate(exerciseMessage.update)
            }

            is ExerciseMessage.AvailabilityChangedMessage -> {
                mergeAvailability(exerciseMessage.dataType, exerciseMessage.availability)
            }

            else -> {}
        }
        return getCurrentExercise()
    }

    fun getCurrentExercise(): CurrentExercise {
        check(currentExercise != null)
        return currentExercise!!
    }

    private fun mergeAvailability(dataType: DataType<*, *>, availability: Availability) {
        check(currentExercise != null)
        if (availability is LocationAvailability) {
            dataAvailability = dataAvailability.copy(
                locationAvailability = availability
            )
        } else if (availability is DataTypeAvailability && dataType == DataType.HEART_RATE_BPM) {
            dataAvailability = dataAvailability.copy(
                heartRateAvailability = availability
            )
        }
        currentExercise = currentExercise!!.copy(
            availability = dataAvailability
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun mergeExerciseUpdate(update: ExerciseUpdate) {
        check(currentExercise != null)
        var changed = false
        val exercise = currentExercise!!
        if (startDateTime == null && update.startTime != null) {
            startDateTime = ZonedDateTime.ofInstant(update.startTime, ZoneId.systemDefault())
            changed = true
        }
        exercise.metrics.forEach { metric ->
            if (metric == TempoMetric.HEART_RATE_BPM) {
                if (dataAvailability.heartRateAvailability.equals(DataTypeAvailability.AVAILABLE)) {
                    update.latestMetrics.getData(DataType.HEART_RATE_BPM)
                        .lastOrNull()?.value?.let { hr ->
                            metricsMap[metric] = hr
                            changed = true
                        }
                } else {
                    metricsMap[metric] = 0.0
                    changed = true
                }
            } else {
                when (metric.aggregationType) {
                    TempoMetric.AggregationType.SAMPLE -> {
                        val dataType =
                            metric.requiredDataType as DeltaDataType<Number, SampleDataPoint<Number>>
                        update.latestMetrics.getData(dataType).lastOrNull()?.value?.let { value ->
                            metricsMap[metric] = value
                            changed = true
                        }
                    }

                    TempoMetric.AggregationType.INTERVAL -> {
                        val dataType =
                            metric.requiredDataType as DeltaDataType<Number, IntervalDataPoint<Number>>
                        update.latestMetrics.getData(dataType).lastOrNull()?.value?.let { value ->
                            metricsMap[metric] = value
                            changed = true
                        }
                    }

                    TempoMetric.AggregationType.MAX -> {
                        val dataType =
                            metric.requiredDataType as AggregateDataType<Number, StatisticalDataPoint<Number>>
                        update.latestMetrics.getData(dataType)?.let { stats ->
                            metricsMap[metric] = stats.max
                            changed = true
                        }
                    }

                    TempoMetric.AggregationType.MIN -> {
                        val dataType =
                            metric.requiredDataType as AggregateDataType<Number, StatisticalDataPoint<Number>>
                        update.latestMetrics.getData(dataType)?.let { stats ->
                            metricsMap[metric] = stats.min
                            changed = true
                        }
                    }

                    TempoMetric.AggregationType.AVG -> {
                        val dataType =
                            metric.requiredDataType as AggregateDataType<Number, StatisticalDataPoint<Number>>
                        update.latestMetrics.getData(dataType)?.let { stats ->
                            metricsMap[metric] = stats.average
                            changed = true
                        }
                    }

                    TempoMetric.AggregationType.TOTAL -> {
                        val dataType =
                            metric.requiredDataType as AggregateDataType<Number, CumulativeDataPoint<Number>>
                        update.latestMetrics.getData(dataType)?.let { cumulative ->
                            metricsMap[metric] = cumulative.total
                            changed = true
                        }
                    }

                    TempoMetric.AggregationType.NONE -> {
                        // Do nothing
                    }
                }
            }
        }
        if (changed) {
            currentExercise = currentExercise!!.copy(
                startDateTime = startDateTime,
                metricsMap = metricsMap
            )
        }
    }
}
