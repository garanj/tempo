package com.garan.tempo.data.metrics

import androidx.health.services.client.data.AggregateDataType
import androidx.health.services.client.data.CumulativeDataPoint
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.IntervalDataPoint
import androidx.health.services.client.data.SampleDataPoint
import androidx.health.services.client.data.StatisticalDataPoint
import com.garan.tempo.data.AvailabilityHolder
import java.util.EnumMap

/**
 * Repository used by the ExerciseService for maintaining the state of metrics, given updates from
 * Health Services.
 */
class MetricsRepository(
    private val metrics: Set<TempoMetric> = setOf()
) {
    // The latest snapshot of metrics, for visualisation in the UI.
    private val metricsMap = EnumMap<TempoMetric, Number>(TempoMetric::class.java)
    // Used to keep track of the state of the HR sensor.
    private var dataAvailability = AvailabilityHolder()

    fun updateAvailability(availabilityHolder: AvailabilityHolder) {
        dataAvailability = availabilityHolder
    }

    @Suppress("UNCHECKED_CAST")
    fun processUpdate(update: ExerciseUpdate): EnumMap<TempoMetric, Number> {
        metrics.forEach { metric ->
            if (metric == TempoMetric.HEART_RATE_BPM) {
                if (dataAvailability.heartRateAvailability.equals(DataTypeAvailability.AVAILABLE)) {
                    update.latestMetrics.getData(DataType.HEART_RATE_BPM)
                        .lastOrNull()?.value?.let { hr ->
                            metricsMap[metric] = hr
                        }
                } else {
                    metricsMap[metric] = 0.0
                }
            } else {
                when (metric.aggregationType) {
                    TempoMetric.AggregationType.SAMPLE -> {
                        val dataType =
                            metric.requiredDataType as DeltaDataType<Number, SampleDataPoint<Number>>
                        update.latestMetrics.getData(dataType).lastOrNull()?.value?.let { value ->
                            metricsMap[metric] = value
                        }
                    }

                    TempoMetric.AggregationType.INTERVAL -> {
                        val dataType =
                            metric.requiredDataType as DeltaDataType<Number, IntervalDataPoint<Number>>
                        update.latestMetrics.getData(dataType).lastOrNull()?.value?.let { value ->
                            metricsMap[metric] = value
                        }
                    }

                    TempoMetric.AggregationType.MAX -> {
                        val dataType =
                            metric.requiredDataType as AggregateDataType<Number, StatisticalDataPoint<Number>>
                        update.latestMetrics.getData(dataType)?.let { stats ->
                            metricsMap[metric] = stats.max
                        }
                    }

                    TempoMetric.AggregationType.MIN -> {
                        val dataType =
                            metric.requiredDataType as AggregateDataType<Number, StatisticalDataPoint<Number>>
                        update.latestMetrics.getData(dataType)?.let { stats ->
                            metricsMap[metric] = stats.min
                        }
                    }

                    TempoMetric.AggregationType.AVG -> {
                        val dataType =
                            metric.requiredDataType as AggregateDataType<Number, StatisticalDataPoint<Number>>
                        update.latestMetrics.getData(dataType)?.let { stats ->
                            metricsMap[metric] = stats.average
                        }
                    }

                    TempoMetric.AggregationType.TOTAL -> {
                        val dataType =
                            metric.requiredDataType as AggregateDataType<Number, CumulativeDataPoint<Number>>
                        update.latestMetrics.getData(dataType)?.let { cumulative ->
                            metricsMap[metric] = cumulative.total
                        }
                    }

                    TempoMetric.AggregationType.NONE -> {
                        // Do nothing
                    }
                }
            }
        }
        return metricsMap
    }
}
