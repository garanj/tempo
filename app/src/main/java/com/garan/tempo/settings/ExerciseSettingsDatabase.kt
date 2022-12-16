package com.garan.tempo.settings

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.garan.tempo.data.SavedExercise
import com.garan.tempo.data.SavedExerciseDao
import com.garan.tempo.data.SavedExerciseMetric

@Database
    (
    entities = [
        ExerciseSettings::class,
        ScreenSettings::class,
        SavedExercise::class,
        SavedExerciseMetric::class,
        TempoSettings::class
    ],
    version = 35
)
@TypeConverters(Converters::class)
abstract class ExerciseSettingsDatabase : RoomDatabase() {
    abstract fun getExerciseSettingsDao(): ExerciseSettingsDao

    abstract fun getSavedExerciseDao(): SavedExerciseDao

    abstract fun getTempoSettingsDao(): TempoSettingsDao
}