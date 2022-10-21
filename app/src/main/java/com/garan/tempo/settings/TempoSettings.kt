package com.garan.tempo.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tempo_settings")
data class TempoSettings(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val units: Units = Units.METRIC
)