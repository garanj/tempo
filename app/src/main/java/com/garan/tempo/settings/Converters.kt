package com.garan.tempo.settings

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import androidx.room.TypeConverter
import com.garan.tempo.data.dataTypes
import com.garan.tempo.data.metrics.TempoMetric
import java.time.Duration
import java.time.ZonedDateTime

class Converters {
    private val dataTypeLookup = dataTypes.map {
        it.name to it
    }.toMap()

    @TypeConverter
    fun fromDuration(duration: Duration): Long = duration.toMillis()

    @TypeConverter
    fun toDuration(millis: Long): Duration = Duration.ofMillis(millis)

    @TypeConverter
    fun fromZonedDateTime(zonedDateTime: ZonedDateTime): String =
        zonedDateTime.toString()

    @TypeConverter
    fun toZonedDateTime(dateString: String): ZonedDateTime =
        ZonedDateTime.parse(dateString)

    @TypeConverter
    fun fromDataTypeSet(dataTypes: Set<DataType<*, *>>): String =
        dataTypes.joinToString(
            separator = ",",
            transform = { dataType -> dataType.name }
        )

    @TypeConverter
    fun toDataTypeSet(encodedString: String): Set<DataType<*, *>> {
        return encodedString.split(",").map {
            dataTypeLookup[it]!!
        }.toSet()
    }

    @TypeConverter
    fun fromTempoMetrics(tempoMetrics: Set<TempoMetric>): String =
        tempoMetrics.joinToString(
            separator = ",",
            transform = { it.name }
        )

    @TypeConverter
    fun toTempoMetrics(encodedString: String): Set<TempoMetric> {
        return encodedString.split(",").map { name ->
            TempoMetric.valueOf(name)
        }.toSet()
    }

    @TypeConverter
    fun fromTempoMetricsList(tempoMetrics: List<TempoMetric>): String =
        tempoMetrics.joinToString(
            separator = ",",
            transform = { it.name }
        )

    @TypeConverter
    fun toTempoMetricsList(encodedString: String): List<TempoMetric> {
        return encodedString.split(",").map { name ->
            TempoMetric.valueOf(name)
        }
    }

    @TypeConverter
    fun toExerciseType(id: Int) = ExerciseType.fromId(id)

    @TypeConverter
    fun fromExerciseType(exerciseType: ExerciseType) = exerciseType.id
}
