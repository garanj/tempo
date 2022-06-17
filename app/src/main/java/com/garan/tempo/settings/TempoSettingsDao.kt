package com.garan.tempo.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TempoSettingsDao {
    @Query("SELECT * FROM tempo_settings")
    fun getTempoSettingsFlow(): Flow<TempoSettings>

    @Query("SELECT * FROM tempo_settings")
    fun getTempoSettings(): TempoSettings

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tempoSettings: TempoSettings)

    @Update
    fun update(tempoSettings: TempoSettings)
}