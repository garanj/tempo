package com.garan.tempo

import android.content.Context
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseInfo
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import com.garan.tempo.data.cachingExerciseClient
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.guava.await
import javax.inject.Inject

class HealthServicesManager @Inject constructor(
    healthServicesClient: HealthServicesClient,
    coroutineScope: CoroutineScope,
    @ApplicationContext appContext: Context
) {
    private val exerciseClient = healthServicesClient.cachingExerciseClient(appContext)

    suspend fun isExerciseInProgress(): ExerciseInfo {
        return exerciseClient.getCurrentExerciseInfoAsync().await()
    }

    suspend fun prepare(exerciseSettingsWithScreens: ExerciseSettingsWithScreens) {
        val warmupDataTypes = setOf<DeltaDataType<*, *>>(
            DataType.LOCATION,
            DataType.HEART_RATE_BPM
        ).intersect(
            exerciseSettingsWithScreens.getRequiredDataTypes()
        )

        @Suppress("UNCHECKED_CAST")
        val config = WarmUpConfig(
            dataTypes = warmupDataTypes as Set<DeltaDataType<*, *>>,
            exerciseType = exerciseSettingsWithScreens.exerciseSettings.exerciseType
        )
        exerciseClient.prepareExerciseAsync(config).await()
    }

    suspend fun endExercise() {
        exerciseClient.endExerciseAsync().await()
    }

    suspend fun pauseExercise() {
        exerciseClient.pauseExerciseAsync().await()
    }

    suspend fun resumeExercise() {
        exerciseClient.resumeExerciseAsync().await()
    }

    suspend fun startExercise(exerciseSettingsWithScreens: ExerciseSettingsWithScreens): String {
        val exerciseSettings = exerciseSettingsWithScreens.exerciseSettings
        val capabilities = exerciseClient.getCapabilitiesAsync().await()
        val exerciseCapabilities =
            capabilities.getExerciseTypeCapabilities(exerciseSettings.exerciseType)

        val dataTypes = exerciseSettingsWithScreens.getRequiredDataTypes()
        val config = ExerciseConfig(
            isGpsEnabled = exerciseSettings.getRequiresGps(),
            isAutoPauseAndResumeEnabled = exerciseSettings.supportsAutoPause && exerciseSettings.useAutoPause,
            dataTypes = dataTypes.intersect(exerciseCapabilities.supportedDataTypes),
            exerciseType = exerciseSettings.exerciseType
        )
        return exerciseClient.startCachedExerciseAsync(config, exerciseSettings.recordingMetrics)
            .await()
    }

    val exerciseUpdateFlow = callbackFlow {
        val listener = object : ExerciseUpdateCallback {
            override fun onAvailabilityChanged(
                dataType: DataType<*, *>,
                availability: Availability
            ) {
                coroutineScope.runCatching {
                    trySendBlocking(
                        ExerciseMessage.AvailabilityChangedMessage(
                            dataType,
                            availability
                        )
                    )
                }
            }

            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                coroutineScope.runCatching {
                    trySendBlocking(ExerciseMessage.ExerciseUpdateMessage(update))
                }
            }

            override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
                coroutineScope.runCatching {
                    trySendBlocking(ExerciseMessage.LapSummaryMessage(lapSummary))
                }
            }

            override fun onRegistered() {
                //
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                //
            }
        }

        exerciseClient.setUpdateCallback(listener)
        awaitClose {
            exerciseClient.clearUpdateCallbackAsync(listener)
        }
    }
}

sealed class ExerciseMessage {
    data class ExerciseUpdateMessage(val update: ExerciseUpdate) : ExerciseMessage()
    data class LapSummaryMessage(val lapSummary: ExerciseLapSummary) : ExerciseMessage()
    data class AvailabilityChangedMessage(
        val dataType: DataType<*, *>,
        val availability: Availability
    ) :
        ExerciseMessage()
}

val ExerciseState.isInProgress: Boolean
    get() = this in setOf(
        ExerciseState.ACTIVE,
        ExerciseState.USER_PAUSING,
        ExerciseState.USER_PAUSED,
        ExerciseState.USER_STARTING,
        ExerciseState.USER_RESUMING,
        ExerciseState.ENDING,
        ExerciseState.AUTO_PAUSING,
        ExerciseState.AUTO_PAUSED,
        ExerciseState.AUTO_RESUMING
    )

val ExerciseState.isUserPaused: Boolean
    get() = this in setOf(
        ExerciseState.USER_PAUSING,
        ExerciseState.USER_PAUSED
    )