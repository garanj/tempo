package com.garan.tempo.cache

import android.content.Context
import android.os.SystemClock
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.HeartRateAccuracy
import androidx.health.services.client.data.IntervalDataPoint
import androidx.health.services.client.data.LocationAccuracy
import androidx.health.services.client.data.LocationData
import androidx.health.services.client.data.SampleDataPoint
import com.garan.counterpart.ExerciseCacheRecord
import com.garan.counterpart.dataTypeAvailability
import com.garan.counterpart.exerciseCacheHeader
import com.garan.counterpart.exerciseCacheRecord
import com.garan.counterpart.heartRateAccuracy
import com.garan.counterpart.intervalExerciseMetric
import com.garan.counterpart.location
import com.garan.counterpart.locationAccuracy
import com.garan.counterpart.locationAvailability
import com.garan.counterpart.sampleExerciseMetric
import com.garan.counterpart.stateRecord
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

const val CACHE_FILE_TEMPLATE = "exercise-cache-%s.proto"

class ExerciseCacheWriter(
    val context: Context,
    val configExerciseType: ExerciseType,
    val requiredDataTypes: Set<DataType<*, *>>,
    val cacheFileId: String = UUID.randomUUID().toString()
) {
    private var lastState = ExerciseState.ENDED
    private var exerciseHasStarted = false
    private val bufferSize = 8192

    private val tempFileName = CACHE_FILE_TEMPLATE.format(cacheFileId)
    private val tempFile = File("${context.cacheDir.absolutePath}/$tempFileName")
    private val tempOutputStream = FileOutputStream(tempFile).buffered(bufferSize)

    private val dataTypeMapping by lazy {
        var idx = 0
        requiredDataTypes.associateWith { ++idx }
    }

    init {
        val serializableDataTypeMapping = dataTypeMapping.mapKeys { it.key.name }
        val exerciseHeader = exerciseCacheHeader {
            bootInstantMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
            dataTypes.putAll(serializableDataTypeMapping)
            exerciseType = configExerciseType.name
            id = UUID.randomUUID().toString()
        }
        val record = exerciseCacheRecord {
            header = exerciseHeader
        }
        record.writeDelimitedTo(tempOutputStream)
    }

    fun processUpdate(exerciseUpdate: ExerciseUpdate) {
        if (exerciseHasStarted && !exerciseUpdate.exerciseStateInfo.state.isPaused) {
            processLatestMetrics(exerciseUpdate.latestMetrics)
        }
        if (lastState != exerciseUpdate.exerciseStateInfo.state) {
            processStateChange(exerciseUpdate)
        }
        lastState = exerciseUpdate.exerciseStateInfo.state
        if (exerciseHasStarted && lastState.isEnded) {
            finalizeCacheFile()
        }
    }

    fun finalizeCacheFile() {
        tempOutputStream.flush()
        tempOutputStream.close()
    }

    fun processAvailability(dataType: DataType<*, *>, availability: Availability) {
        val cacheDataType = dataTypeMapping[dataType] ?: return
        if (dataTypeMapping.containsKey(dataType)) {
            val cacheRecord = if (dataType == DataType.LOCATION) {
                exerciseCacheRecord {
                    locationAvailability = locationAvailability {
                        id = availability.id
                    }
                }
            } else {
                exerciseCacheRecord {
                    dataTypeAvailability = dataTypeAvailability {
                        id = availability.id
                        this.dataType = cacheDataType
                    }
                }
            }
            cacheRecord.writeDelimitedTo(tempOutputStream)
        }
    }

    fun processLapSummary(lapSummary: ExerciseLapSummary) {

    }

    private fun processLatestMetrics(metrics: DataPointContainer) {
        metrics.sampleDataPoints
            .mapNotNull { it.toCachePoint() }
            .forEach { it.writeDelimitedTo(tempOutputStream) }
        metrics.intervalDataPoints
            .mapNotNull { it.toCachePoint() }
            .forEach { it.writeDelimitedTo(tempOutputStream) }
    }

    private fun processStateChange(exerciseUpdate: ExerciseUpdate) {
        val newState = exerciseUpdate.exerciseStateInfo.state
        if (!exerciseHasStarted && newState == ExerciseState.ACTIVE) {
            exerciseHasStarted = true
        }
        if ((lastState == ExerciseState.ACTIVE && newState.isInactive) ||
            (newState == ExerciseState.ACTIVE && lastState.isInactive)
        ) {
            val exerciseCacheRecord = exerciseCacheRecord {
                state = stateRecord {
                    timestamp = exerciseUpdate.activeDurationCheckpoint!!.time.toEpochMilli()
                    state = newState.id
                }
            }
            exerciseCacheRecord.writeDelimitedTo(tempOutputStream)
        }
    }

    private fun <T : Any> SampleDataPoint<T>.toCachePoint(): ExerciseCacheRecord? {
        val dp = this
        val dataTypeIndex = this@ExerciseCacheWriter.dataTypeMapping[this.dataType] ?: return null
        return exerciseCacheRecord {
            sample = sampleExerciseMetric {
                timeFromBoot = dp.timeDurationFromBoot.toMillis()
                dataType = dataTypeIndex
                dp.accuracy?.let { accuracy ->
                    when (accuracy) {
                        is LocationAccuracy -> locationAccuracy = locationAccuracy {
                            if (accuracy.horizontalPositionErrorMeters != Double.MAX_VALUE) {
                                horizontalPositionErrorMeters =
                                    accuracy.horizontalPositionErrorMeters
                            }
                            if (accuracy.verticalPositionErrorMeters != Double.MAX_VALUE) {
                                verticalPositionErrorMeters = accuracy.verticalPositionErrorMeters
                            }
                        }
                    }
                }
                when (val value = dp.value) {
                    is Double -> doubleValue = value
                    is Long -> longValue = value
                    is LocationData -> locationValue = location {
                        latitude = value.latitude
                        longitude = value.longitude
                        if (!value.altitude.isNaN() && value.altitude != Double.MAX_VALUE) {
                            altitude = value.altitude
                        }
                        if (!value.bearing.isNaN() && value.bearing != Double.MAX_VALUE) {
                            bearing = value.bearing
                        }
                    }
                }
            }
        }
    }

    private fun <T : Any> IntervalDataPoint<T>.toCachePoint(): ExerciseCacheRecord? {
        val dp = this
        val dataTypeIndex = this@ExerciseCacheWriter.dataTypeMapping[this.dataType] ?: return null
        return exerciseCacheRecord {
            interval = intervalExerciseMetric {
                startTimeFromBoot = dp.startDurationFromBoot.toMillis()
                endTimeFromBoot = dp.endDurationFromBoot.toMillis()
                dataType = dataTypeIndex
                dp.accuracy?.let { accuracy ->
                    when (accuracy) {
                        is HeartRateAccuracy -> heartAccuracy = heartRateAccuracy {
                            id = accuracy.sensorStatus.id
                        }
                    }
                }
                when (val value = dp.value) {
                    is Double -> doubleValue = value
                    is Long -> longValue = value
                }
            }
        }
    }
}

val ExerciseState.isInactive: Boolean
    get() = this.isEnded || this.isPaused
