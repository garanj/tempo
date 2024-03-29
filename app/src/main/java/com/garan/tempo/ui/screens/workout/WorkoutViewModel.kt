package com.garan.tempo.ui.screens.workout

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.lifecycle.ViewModel
import com.garan.tempo.TAG
import com.garan.tempo.TempoService
import com.garan.tempo.data.metrics.CurrentExercise
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.vibrations.TempoVibrationsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val tempoVibrationsManager: TempoVibrationsManager
) : ViewModel() {
    private var tempoService: TempoService? = null

    val serviceState: MutableState<ServiceState> = mutableStateOf(ServiceState.Disconnected)
    var bound = mutableStateOf(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TempoService.LocalBinder
            binder.getService().let {
                tempoService = it
                serviceState.value = ServiceState.Connected(
                    exerciseState = it.exerciseState,
                    exercise = it.currentExercise,
                    checkpoint = it.checkpoint,
                    settings = it.currentSettings
                )
            }
            Log.i(TAG, "onServiceConnected")
            bound.value = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound.value = false
            tempoService = null
            serviceState.value = ServiceState.Disconnected
            Log.i(TAG, "onServiceDisconnected")
        }
    }

    init {
        if (!bound.value) {
            createService()
        }
    }

    fun endExercise() = tempoService?.endExercise()

    fun pauseResumeExercise() = tempoService?.pauseResumeExercise()

    fun onPagerChange() = tempoVibrationsManager.vibrateForScroll()

    private fun createService() {
        Intent(applicationContext, TempoService::class.java).also { intent ->
            applicationContext.startForegroundService(intent)
            applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (bound.value) {
            applicationContext.unbindService(connection)
        }
    }
}

sealed class ServiceState {
    object Disconnected : ServiceState()
    data class Connected(
        val exerciseState: State<ExerciseState>,
        val exercise: State<CurrentExercise>,
        val checkpoint: State<ExerciseUpdate.ActiveDurationCheckpoint?>,
        val settings: State<ExerciseSettingsWithScreens?>
    ) : ServiceState()
}