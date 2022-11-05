package com.garan.tempo.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface SavedExerciseDao {
    @Transaction
    @Query("SELECT * FROM saved_exercises where recordingId = :recordingId")
    fun getSavedExercise(recordingId: String): Flow<SavedExerciseWithMetrics>

    @Insert(onConflict = REPLACE)
    suspend fun insert(savedExercise: SavedExercise): Long

    @Update(entity = SavedExercise::class)
    fun update(savedExerciseUpdate: SavedExerciseUpdate)

    @Insert(onConflict = REPLACE)
    suspend fun insert(metric: SavedExerciseMetric)

    @Transaction
    @Insert
    suspend fun insert(savedExercise: SavedExercise, metrics: List<SavedExerciseMetric>) {
        val id = insert(savedExercise)
        metrics.forEach { metric ->
            metric.exerciseId = id
            insert(metric)
        }
    }
}

@Entity
data class SavedExerciseUpdate(
    @ColumnInfo(name = "exerciseId")
    var exerciseId: String = "",

    @ColumnInfo(name = "hasMap")
    val hasMap: Boolean = false
)