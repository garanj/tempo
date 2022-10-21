package com.garan.tempo.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface SavedExerciseDao {
    @Query("SELECT * FROM saved_exercises where exerciseId = :exerciseId")
    fun getSavedExercise(exerciseId: String): Flow<SavedExercise>

    @Insert(onConflict = REPLACE)
    suspend fun insert(savedExercise: SavedExercise)

    @Update(entity = SavedExercise::class)
    fun update(savedExerciseUpdate: SavedExerciseUpdate)
}

@Entity
data class SavedExerciseUpdate(
    @ColumnInfo(name = "exerciseId")
    var exerciseId: String = "",

    @ColumnInfo(name = "hasMap")
    val hasMap: Boolean = false
)