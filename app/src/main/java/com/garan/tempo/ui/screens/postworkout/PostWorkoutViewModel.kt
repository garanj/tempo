package com.garan.tempo.ui.screens.postworkout

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.garan.tempo.TAG
import com.garan.tempo.TempoService
import com.garan.tempo.data.SavedExerciseDao
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.ui.screens.workout.ServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PostWorkoutViewModel @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    savedExerciseDao: SavedExerciseDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val exerciseId = savedStateHandle.get<String>("exerciseId")!!

    val savedExercise = savedExerciseDao.getSavedExercise(exerciseId)
}