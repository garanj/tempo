package com.garan.tempo.data

import android.util.Log
import androidx.health.services.client.data.Value
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.garan.tempo.TAG
import com.garan.tempo.ui.metrics.DisplayMetric
import kotlinx.coroutines.flow.Flow


@Dao
interface SavedExerciseDao {
    @Transaction
    @Query("SELECT * FROM saved_exercises where exerciseId = :exerciseId")
    fun getSavedExercise(exerciseId: String): Flow<SavedExercise>

    @Insert(onConflict = REPLACE)
    suspend fun insert(savedExercise: SavedExercise)

    @Insert
    suspend fun insert(savedExerciseMetricCache: SavedExerciseMetricCache)

    @Query("SELECT * FROM exercise_metric_cache")
    fun getExerciseMetricCache(): Flow<SavedExerciseMetricCache>

    @Query("DELETE FROM exercise_metric_cache")
    fun deleteAllExerciseMetricCache()
}