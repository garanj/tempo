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
    fun insert(savedExercise: SavedExercise): Long

    @Update(entity = SavedExercise::class)
    suspend fun update(savedExerciseUpdate: SavedExerciseUpdate)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(metrics: List<SavedExerciseMetric>)

    @Transaction
    @Insert
    suspend fun insert(savedExercise: SavedExercise, metrics: List<SavedExerciseMetric>): Long {
        val id = insert(savedExercise)
        metrics.forEach { it.exerciseId = id }
        insertAll(metrics)
        return id
    }
}

@Entity
data class SavedExerciseUpdate(
    @ColumnInfo(name = "exerciseId")
    var exerciseId: Long = 0,

    @ColumnInfo(name = "mapPathData")
    val mapPathData: ByteArray? = null
)