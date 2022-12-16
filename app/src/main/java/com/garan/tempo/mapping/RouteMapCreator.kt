package com.garan.tempo.mapping

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.garan.tempo.TAG
import com.garan.tempo.cache.ExerciseCacheReader
import com.garan.tempo.data.SavedExerciseDao
import com.garan.tempo.data.SavedExerciseUpdate
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan


const val CACHE_FILE_ID = "cache_file_id"
const val DATABASE_WORKOUT_ID = "workout_id"

class RouteMapCreator @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun createMap(cacheFileId: String, databaseWorkoutId: Long) {
        val inputData = Data.Builder()
            .putString(CACHE_FILE_ID, cacheFileId)
            .putLong(DATABASE_WORKOUT_ID, databaseWorkoutId)
            .build()
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<MapCreatorWorker>()
                .setInputData(inputData)
                .build()
        WorkManager
            .getInstance(context)
            .enqueue(uploadWorkRequest)
    }
}

@HiltWorker
class MapCreatorWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val dao: SavedExerciseDao,
) :
    CoroutineWorker(appContext, workerParams) {

    private val MIN_DATA_POINTS = 5
    private val RADIUS_MAJOR = 6378137.0
    private val RADIUS_MINOR = 6356752.3142

    override suspend fun doWork(): Result {
        val cacheFileId = workerParams.inputData.getString(CACHE_FILE_ID)
        val workoutId = workerParams.inputData.getLong(DATABASE_WORKOUT_ID, 0L)
        if (cacheFileId == null || workoutId == 0L) {
            Log.w(TAG, "Invalid parameters!")
            return Result.success()
        }

        val reader = ExerciseCacheReader(appContext, cacheFileId)

        val latLongs = reader.getRecords()
            .filter { it.hasSample() && it.sample.hasLocationValue() }
            .map { it.sample.locationValue }
            .filterIndexed { index, _ ->
                // As a very crude filter only take 1 in 10 points, given it's a thumbnail map only
                index % 10 == 0
            }.map {
                val x = xAxisProjection(it.longitude)
                val y = yAxisProjection(it.latitude)
                LatLng(lng = x, lat = y)
            }.toList()

        if (latLongs.size > MIN_DATA_POINTS) {
            val bufferSizeBytes = latLongs.size * 2 * Double.SIZE_BYTES
            val buffer = ByteBuffer.allocate(bufferSizeBytes)
            latLongs.forEach {
                buffer.putDouble(it.lng)
                buffer.putDouble(it.lat)
            }

            dao.update(SavedExerciseUpdate(workoutId, buffer.array()))
        }
        return Result.success()
    }

    /**
     * Projects a latitude using Mercator projection.
     */
    fun yAxisProjection(value: Double): Double {
        val input = value.coerceIn(-89.5, 89.5)
        val earthDimensionalRateNormalized = 1.0 - (RADIUS_MINOR / RADIUS_MAJOR).pow(2.0)
        var inputOnEarthProj = sqrt(earthDimensionalRateNormalized) *
                sin(Math.toRadians(input))
        inputOnEarthProj = ((1.0 - inputOnEarthProj) / (1.0 + inputOnEarthProj)).pow(
            0.5 * sqrt(earthDimensionalRateNormalized)
        )
        val inputOnEarthProjNormalized =
            tan(0.5 * (Math.PI * 0.5 - Math.toRadians(input))) / inputOnEarthProj
        return -1 * RADIUS_MAJOR * ln(inputOnEarthProjNormalized)
    }

    /**
     * Projects a longitude using Mercator projecction.
     */
    fun xAxisProjection(input: Double): Double {
        return RADIUS_MAJOR * Math.toRadians(input)
    }
}