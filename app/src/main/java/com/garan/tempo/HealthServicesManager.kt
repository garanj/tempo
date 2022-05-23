package com.garan.tempo

import androidx.health.services.client.ExerciseUpdateListener
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseInfo
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.guava.await
import javax.inject.Inject

class HealthServicesManager @Inject constructor(
    healthServicesClient: HealthServicesClient,
    coroutineScope: CoroutineScope
) {
    private val exerciseClient = healthServicesClient.exerciseClient

    suspend fun isExerciseInProgress(): ExerciseInfo {
        return exerciseClient.currentExerciseInfo.await()
    }

    suspend fun prepare(exerciseSettingsWithScreens: ExerciseSettingsWithScreens) {
        val warmupDataTypes = setOf(
            DataType.LOCATION,
            DataType.HEART_RATE_BPM
        ).intersect(
            exerciseSettingsWithScreens.getRequiredDataTypes().first
        )

        val config = WarmUpConfig.builder()
            .setDataTypes(warmupDataTypes)
            .setExerciseType(exerciseSettingsWithScreens.exerciseSettings.exerciseType)
            .build()
        exerciseClient.prepareExercise(config).await()
    }

    suspend fun endExercise() {
        exerciseClient.endExercise().await()
    }

    suspend fun pauseExercise() {
        exerciseClient.pauseExercise().await()
    }

    suspend fun resumeExercise() {
        exerciseClient.resumeExercise().await()
    }

    suspend fun startExercise(exerciseSettingsWithScreens: ExerciseSettingsWithScreens) {
        val exerciseSettings = exerciseSettingsWithScreens.exerciseSettings
        val capabilities = exerciseClient.capabilities.await()
        val exerciseCapabilities =
            capabilities.getExerciseTypeCapabilities(exerciseSettings.exerciseType)

        val (dataTypes, aggregateDataTypes) = exerciseSettingsWithScreens.getRequiredDataTypes()
        val config = ExerciseConfig.builder()
            .setShouldEnableGps(exerciseSettings.getRequiresGps())
            .apply {
                if (exerciseSettings.supportsAutoPause) {
                    setShouldEnableAutoPauseAndResume(
                        exerciseSettings.useAutoPause
                    )
                }
            }
            .setDataTypes(dataTypes.intersect(exerciseCapabilities.supportedDataTypes))
            .setAggregateDataTypes(aggregateDataTypes.intersect(exerciseCapabilities.supportedDataTypes))
            .setExerciseType(exerciseSettings.exerciseType)
            .build()
        exerciseClient.startExercise(config).await()

    }

    val exerciseUpdateFlow = callbackFlow {
        val listener = object : ExerciseUpdateListener {
            override fun onAvailabilityChanged(dataType: DataType, availability: Availability) {
                coroutineScope.runCatching {
                    trySendBlocking(
                        ExerciseMessage.AvailabilityChangedMessage(
                            dataType,
                            availability
                        )
                    )
                }
            }

            override fun onExerciseUpdate(update: ExerciseUpdate) {
                coroutineScope.runCatching {
                    trySendBlocking(ExerciseMessage.ExerciseUpdateMessage(update))
                }
            }

            override fun onLapSummary(lapSummary: ExerciseLapSummary) {
                coroutineScope.runCatching {
                    trySendBlocking(ExerciseMessage.LapSummaryMessage(lapSummary))
                }
            }
        }

        exerciseClient.setUpdateListener(listener)
        awaitClose {
            exerciseClient.clearUpdateListener(listener)
        }
    }
}

sealed class ExerciseMessage {
    data class ExerciseUpdateMessage(val update: ExerciseUpdate) : ExerciseMessage()
    data class LapSummaryMessage(val lapSummary: ExerciseLapSummary) : ExerciseMessage()
    data class AvailabilityChangedMessage(val dataType: DataType, val availability: Availability) :
        ExerciseMessage()
}

val ExerciseState.isInProgress: Boolean
    get() = this in setOf(
        ExerciseState.ACTIVE,
        ExerciseState.USER_PAUSING,
        ExerciseState.USER_PAUSED,
        ExerciseState.USER_STARTING,
        ExerciseState.USER_RESUMING,
        ExerciseState.USER_ENDING,
        ExerciseState.AUTO_PAUSING,
        ExerciseState.AUTO_PAUSED,
        ExerciseState.AUTO_RESUMING,
        ExerciseState.AUTO_ENDING,
        ExerciseState.AUTO_ENDING_PERMISSION_LOST,
        ExerciseState.TERMINATING
    )

val ExerciseState.isUserPaused: Boolean
    get() = this in setOf(
        ExerciseState.USER_PAUSING,
        ExerciseState.USER_PAUSED
    )