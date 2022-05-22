package com.garan.tempo.settings

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database
    (
    entities = [
        ExerciseSettings::class,
        ScreenSettings::class
    ],
    version = 19
)
@TypeConverters(Converters::class)
abstract class ExerciseSettingsDatabase : RoomDatabase() {
    abstract fun getExerciseSettingsDao(): ExerciseSettingsDao

    abstract fun getTempoSettingsDao(): TempoSettingsDao
}