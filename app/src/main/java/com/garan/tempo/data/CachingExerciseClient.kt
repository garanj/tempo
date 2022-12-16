package com.garan.tempo.data

import android.content.Context
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseGoal
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import com.garan.tempo.cache.ExerciseCacheWriter
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.Executor

fun HealthServicesClient.cachingExerciseClient(context: Context) =
    CachingExerciseClient(context, this.exerciseClient)

class CachingExerciseClient(
    private val context: Context,
    private val exerciseClient: ExerciseClient
) : ExerciseClient {
    private var cacheWriter: ExerciseCacheWriter? = null
    private var originalCallback: ExerciseUpdateCallback? = null

    private val cachingCallback = object : ExerciseUpdateCallback {
        override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: Availability) {
            cacheWriter?.processAvailability(dataType, availability)
            originalCallback?.onAvailabilityChanged(dataType, availability)
        }

        override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
            cacheWriter?.processUpdate(update)
            originalCallback?.onExerciseUpdateReceived(update)
        }

        override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
            cacheWriter?.processLapSummary(lapSummary)
            originalCallback?.onLapSummaryReceived(lapSummary)
        }

        override fun onRegistered() {
            originalCallback?.onRegistered()
        }

        override fun onRegistrationFailed(throwable: Throwable) {
            originalCallback?.onRegistrationFailed(throwable)
        }
    }

    override fun setUpdateCallback(callback: ExerciseUpdateCallback) {
        originalCallback = callback
        exerciseClient.setUpdateCallback(cachingCallback)
    }

    override fun setUpdateCallback(executor: Executor, callback: ExerciseUpdateCallback) {
        originalCallback = callback
        exerciseClient.setUpdateCallback(executor, cachingCallback)
    }

    fun startCachedExerciseAsync(
        configuration: ExerciseConfig,
        requiredDataTypes: Set<DataType<*, *>>
    ): ListenableFuture<String> {
        cacheWriter = ExerciseCacheWriter(context, configuration.exerciseType, requiredDataTypes)
        val futureToObserve = exerciseClient.startExerciseAsync(configuration)
        return Futures.transform(
            futureToObserve,
            { cacheWriter!!.cacheFileId },
            MoreExecutors.directExecutor()
        )
    }


    override fun addGoalToActiveExerciseAsync(exerciseGoal: ExerciseGoal<*>) =
        exerciseClient.addGoalToActiveExerciseAsync(exerciseGoal)

    override fun clearUpdateCallbackAsync(callback: ExerciseUpdateCallback) =
        exerciseClient.clearUpdateCallbackAsync(callback)

    override fun endExerciseAsync() = exerciseClient.endExerciseAsync()

    override fun flushAsync() = exerciseClient.flushAsync()

    override fun getCapabilitiesAsync() = exerciseClient.getCapabilitiesAsync()

    override fun getCurrentExerciseInfoAsync() = exerciseClient.getCurrentExerciseInfoAsync()

    override fun markLapAsync() = exerciseClient.markLapAsync()

    override fun overrideAutoPauseAndResumeForActiveExerciseAsync(enabled: Boolean) =
        exerciseClient.overrideAutoPauseAndResumeForActiveExerciseAsync(enabled)

    override fun pauseExerciseAsync() = exerciseClient.pauseExerciseAsync()

    override fun prepareExerciseAsync(configuration: WarmUpConfig) =
        exerciseClient.prepareExerciseAsync(configuration)

    override fun removeGoalFromActiveExerciseAsync(exerciseGoal: ExerciseGoal<*>) =
        exerciseClient.removeGoalFromActiveExerciseAsync(exerciseGoal)

    override fun resumeExerciseAsync() = exerciseClient.resumeExerciseAsync()

    override fun startExerciseAsync(configuration: ExerciseConfig) =
        exerciseClient.startExerciseAsync(configuration)
}