package com.garan.tempo.settings

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.garan.tempo.TAG
import com.garan.tempo.settings.defaults.defaultExerciseSettingsList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class TempoSettingsManager(private val context: Context) {
    private val TEMPO_UNITS = booleanPreferencesKey("tempo_units")
    private val EXERCISE_SETTINGS_LIST = stringPreferencesKey("exercise_settings_list")

    @ExperimentalSerializationApi
    val exercises: Flow<List<ExerciseSettings>> = context.settingsDataStore.data.map { settings ->
        val exerciseSettingsString = settings[EXERCISE_SETTINGS_LIST]
        if (exerciseSettingsString.isNullOrEmpty()) {
            val defaults = defaultExerciseSettingsList()
            context.settingsDataStore.edit { newSettings ->
                newSettings[EXERCISE_SETTINGS_LIST] = Json.encodeToString(defaults)
            }
            defaults
        } else {
            Json.decodeFromString<List<ExerciseSettings>>(exerciseSettingsString)
        }
    }

    val units: Flow<Units> = context.settingsDataStore.data.map { preferences ->
        when(preferences[TEMPO_UNITS]) {
            true -> Units.IMPERIAL
            else -> Units.METRIC
        }
    }

    suspend fun setUnits(units: Units) {
        context.settingsDataStore.edit { preferences ->
            Log.i(TAG, "Setting value; $units")
            preferences[TEMPO_UNITS] = when(units) {
                Units.METRIC -> false
                Units.IMPERIAL -> true
            }
        }
    }
}

enum class Units {
    METRIC,
    IMPERIAL
}