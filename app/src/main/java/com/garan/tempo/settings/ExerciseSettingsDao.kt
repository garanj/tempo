package com.garan.tempo.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ExerciseSettingsDao {
    @Transaction
    @Query("SELECT * FROM exercise_settings")
    fun getExerciseSettingsWithScreenSettings(): Flow<List<ExerciseSettingsWithScreens>>

    @Transaction
    @Query("SELECT * FROM exercise_settings where exerciseSettingsId = :settingsId")
    fun getExerciseSettingsWithScreenSettings(settingsId: Int): Flow<ExerciseSettingsWithScreens>

    @Query("SELECT * FROM exercise_settings where exerciseSettingsId = :settingsId")
    fun getExerciseSettings(settingsId: Int): ExerciseSettings

    @Transaction
    @Insert
    suspend fun insert(exerciseSettings: ExerciseSettings, screenSettings: List<ScreenSettings>) {
        val id = insert(exerciseSettings)
        screenSettings.forEachIndexed { index, settings ->
            settings.exerciseSettingsId = id
            settings.screenIndex = index
            insert(settings)
        }
    }

    @Insert(onConflict = REPLACE)
    suspend fun insert(exerciseSettings: ExerciseSettings?): Long

    @Insert(onConflict = REPLACE)
    suspend fun insert(screenSettings: ScreenSettings?)

    @Update
    suspend fun updateScreenSettings(screenSettings: ScreenSettings?)

    @Update
    suspend fun updateExerciseSettings(exerciseSettings: ExerciseSettings)

    @Query("SELECT * FROM screen_settings where exerciseSettingsId = :settingsId and screenIndex = :screen")
    suspend fun getScreen(settingsId: Int, screen: Int): ScreenSettings
}