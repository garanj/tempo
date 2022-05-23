package com.garan.tempo.ui.screens.preworkout

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
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.ui.screens.workout.ServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PreWorkoutViewModel @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    val tempoSettingsManager: TempoSettingsManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val settingsId = savedStateHandle.get<Int>("settingsId")!!
    private var tempoService: TempoService? = null
    val exerciseSettings = tempoSettingsManager.getExerciseSettings(settingsId)

    val serviceState: MutableState<ServiceState> = mutableStateOf(ServiceState.Disconnected)
    var bound = mutableStateOf(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TempoService.LocalBinder
            binder.getService().let {
                tempoService = it
                serviceState.value = ServiceState.Connected(
                    exerciseState = it.exerciseState,
                    hrAvailability = it.hrAvailability,
                    locationAvailability = it.locationAvailability,
                    metrics = it.metrics
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
        Log.i(TAG, "**** $this")
        if (!bound.value) {
            createService()
        }
    }

    fun prepare() = tempoService?.prepare(settingsId)

    fun startExercise() = tempoService?.startExercise(settingsId)

    private fun createService() {
        Intent(applicationContext, TempoService::class.java).also { intent ->
            applicationContext.startForegroundService(intent)
            applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (bound.value) {
            Log.i(TAG, "* clear Unbinding")
            applicationContext.unbindService(connection)
        }
    }
}