package com.garan.tempo.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.garan.tempo.data.metrics.TempoMetric

@Entity(tableName = "screen_settings")
data class ScreenSettings(
    @PrimaryKey(autoGenerate = true)
    var screenSettingsId: Int? = null,
    var screenIndex: Int = 0,
    var exerciseSettingsId: Long = 0,
    val screenFormat: ScreenFormat = ScreenFormat.SIX_SLOT,
    val metrics: List<TempoMetric> = listOf()
)
