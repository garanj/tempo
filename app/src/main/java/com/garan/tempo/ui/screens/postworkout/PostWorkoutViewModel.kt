package com.garan.tempo.ui.screens.postworkout

import android.content.Context
import androidx.lifecycle.ViewModel
import com.garan.tempo.data.SavedExerciseDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PostWorkoutViewModel @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    val savedExerciseDao: SavedExerciseDao
) : ViewModel() {
    fun savedExercise(exerciseId: String) = savedExerciseDao.getSavedExercise(exerciseId)
}