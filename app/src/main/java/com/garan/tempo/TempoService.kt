package com.garan.tempo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.core.app.NotificationCompat
import androidx.health.services.client.data.CumulativeDataPoint
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.LocationAvailability
import androidx.health.services.client.data.StatisticalDataPoint
import androidx.health.services.client.data.Value
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.ui.metrics.AggregationType
import com.garan.tempo.ui.metrics.DisplayMetric
import com.garan.tempo.vibrations.stateVibrations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

typealias DisplayUpdateMap = SnapshotStateMap<DisplayMetric, Value>

@AndroidEntryPoint
class TempoService : LifecycleService() {
    @Inject
    lateinit var tempoSettingsManager: TempoSettingsManager

    @Inject
    lateinit var healthManager: HealthServicesManager

    private val binder = LocalBinder()
    private var started = false

    private val vibrator by lazy { getSystemService(VIBRATOR_SERVICE) as Vibrator }

    private val _exerciseState: MutableState<ExerciseState> =
        mutableStateOf(ExerciseState.USER_ENDED)
    val exerciseState: State<ExerciseState> = _exerciseState

    private val _locationAvailability: MutableState<LocationAvailability> =
        mutableStateOf(LocationAvailability.UNKNOWN)
    val locationAvailability: State<LocationAvailability> = _locationAvailability

    private val _hrAvailability: MutableState<DataTypeAvailability> =
        mutableStateOf(DataTypeAvailability.UNKNOWN)
    val hrAvailability: State<DataTypeAvailability> = _hrAvailability

    var currentSettings: ExerciseSettingsWithScreens? = null

    val metrics: DisplayUpdateMap = mutableStateMapOf()

    // TODO - why is finish not coming through???

    private fun processExerciseUpdateForDisplay(
        metrics: Set<DisplayMetric>,
        metricsMap: DisplayUpdateMap,
        update: ExerciseUpdate
    ) {
        metrics.forEach { metric ->
            when (metric.aggregationType()) {
                AggregationType.SAMPLE -> {
                    update.latestMetrics[metric.requiredDataType()]?.last()?.let { dataPoint ->
                        metricsMap[metric] = dataPoint.value
                    }
                }
                AggregationType.TOTAL -> {
                    update.latestAggregateMetrics[metric.requiredDataType()]?.let { aggregate ->
                        metricsMap[metric] = (aggregate as CumulativeDataPoint).total
                    }
                }
                AggregationType.AVG -> {
                    update.latestAggregateMetrics[metric.requiredDataType()]?.let { aggregate ->
                        metricsMap[metric] = (aggregate as StatisticalDataPoint).average
                    }
                }
                AggregationType.MIN -> {
                    update.latestAggregateMetrics[metric.requiredDataType()]?.let { aggregate ->
                        metricsMap[metric] = (aggregate as StatisticalDataPoint).min
                    }
                }
                AggregationType.MAX -> {
                    update.latestAggregateMetrics[metric.requiredDataType()]?.let { aggregate ->
                        metricsMap[metric] = (aggregate as StatisticalDataPoint).max
                    }
                }
                AggregationType.NONE -> {
                    if (metric == DisplayMetric.ACTIVE_DURATION) {
                        metricsMap[metric] = Value.ofLong(update.activeDuration.seconds)
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (!started) {
            enableForegroundService()
            started = true

            lifecycleScope.launch {
                healthManager.exerciseUpdateFlow.collect { message ->
                    when (message) {
                        is ExerciseMessage.ExerciseUpdateMessage -> {
                            currentSettings?.let { current ->
                                processExerciseUpdateForDisplay(
                                    current.getDisplayMetricsSet(),
                                    metrics,
                                    message.update
                                )
                                vibrateByStateTransition(message.update.state)
                            }
                            _exerciseState.value = message.update.state
                        }
                        is ExerciseMessage.AvailabilityChangedMessage -> {
                            if (message.availability is LocationAvailability) {
                                _locationAvailability.value = message.availability
                            } else if (message.availability is DataTypeAvailability
                                && message.dataType == DataType.HEART_RATE_BPM
                            ) {
                                _hrAvailability.value = message.availability
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        }
        Log.i(TAG, "service onStartCommand")
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.i(TAG, "service onBind")
        return binder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "service onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "* service onUnbind")
        if (!_exerciseState.value.isInProgress) {
            maybeStopService()
        }
        return true
    }

    fun prepare() {
        lifecycleScope.launch {
            healthManager.prepare()
        }
    }

    fun cancelPrepare() {
        lifecycleScope.launch {
            healthManager.endExercise()
        }
    }

    @ExperimentalSerializationApi
    fun startExercise(settingsId: Int) {
        lifecycleScope.launch {
            currentSettings = tempoSettingsManager.getExerciseSettings(settingsId).first()
            healthManager.startExercise(currentSettings!!)
        }
    }

    fun endExercise() {
        lifecycleScope.launch {
            Log.i(TAG, "Ending exercise")
            healthManager.endExercise()
        }
    }

    fun pauseResumeExercise() {
        lifecycleScope.launch {
            if (exerciseState.value.isUserPaused) {
                healthManager.resumeExercise()
            } else {
                healthManager.pauseExercise()
            }
        }
    }

    private fun vibrateByStateTransition(state: ExerciseState) {
        stateVibrations.get(state)?.let {
            vibrator.vibrate(it)
        }
    }

    private fun maybeStopService() {
        Log.i(TAG, "* Stopping service")
        lifecycleScope.launch {
            if (!_exerciseState.value.isEnded) {
                // For example, if still in the PREPARING state.
                healthManager.endExercise()
            }
        }
        stopSelf()
    }

    private fun enableForegroundService() {
        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL, "com.garan.tempo.ONGOING",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, TempoActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Build the notification.
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val ongoingActivityStatus = Status.Builder()
            .addTemplate(STATUS_TEMPLATE)
            //.addPart("speed", Status.TextPart("${metrics.speedToDevice.value}"))
            .build()
        val ongoingActivity =
            OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
                .setAnimatedIcon(R.drawable.ic_launcher_foreground)
                .setStaticIcon(R.drawable.ic_launcher_foreground)
                .setTouchIntent(pendingIntent)
                .setStatus(ongoingActivityStatus)
                .build()
        ongoingActivity.apply(applicationContext)

        return notificationBuilder.build()
    }

    inner class LocalBinder : Binder() {
        fun getService(): TempoService = this@TempoService
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL = "com.garan.tempo.TempoService"
        const val NOTIFICATION_TITLE = "Tempo"
        const val NOTIFICATION_TEXT = "Tempo"
        const val STATUS_TEMPLATE = "Tempo"
    }
}
