package com.garan.tempo.settings

import androidx.health.services.client.data.DataType
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.garan.tempo.data.requiredPermissions

data class ExerciseSettingsWithScreens(
    @Embedded val exerciseSettings: ExerciseSettings = ExerciseSettings(),
    @Relation(
        parentColumn = "exerciseSettingsId",
        entityColumn = "exerciseSettingsId"
    )
    val screenSettings: List<ScreenSettings> = listOf()
) {
    @Ignore
    val displayMetricsSet = screenSettings.flatMap { it.metrics }.toSet()

    fun getRequiredDataTypes(): Set<DataType<*, *>> {
        // Start with the set of metrics required for recording (i.e. those that will be written to
        // database, but aren't for UI display necessarily.
        val dataTypes = exerciseSettings.recordingMetrics.toMutableSet()

        screenSettings.forEach { screenSetting ->
            // Only take first n metrics based on the type of screen format
            // it is.
            screenSetting.metrics.subList(0, screenSetting.screenFormat.numSlots)
                .forEach { displayMetric ->
                    displayMetric.requiredDataType?.let {
                        dataTypes.add(it)
                    }
                }
        }
        // Ensure data types for the metrics to be shown in the workout summary post-workout are
        // added.
        exerciseSettings.endSummaryMetrics
            .forEach { displayMetric ->
                displayMetric.requiredDataType?.let { dataTypes.add(it) }
            }
        return dataTypes
    }

    fun getRequiredPermissions() = getRequiredDataTypes().map { dataType ->
        dataType.requiredPermissions
    }.flatten().toSet()
}