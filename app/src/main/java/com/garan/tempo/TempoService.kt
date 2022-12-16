package com.garan.tempo

//import com.garan.tempo.data.ExerciseCache
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.core.app.NotificationCompat
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.garan.tempo.data.SavedExercise
import com.garan.tempo.data.SavedExerciseDao
import com.garan.tempo.data.SavedExerciseMetric
import com.garan.tempo.data.isAutoPauseState
import com.garan.tempo.data.metrics.CurrentExercise
import com.garan.tempo.data.metrics.CurrentExerciseRepository
import com.garan.tempo.mapping.RouteMapCreator
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.vibrations.TempoVibrationsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class TempoService : LifecycleService() {
    // Settings that apply to the whole app, e.g. which units to use.
    @Inject
    lateinit var tempoSettingsManager: TempoSettingsManager

    @Inject
    lateinit var tempoVibrationsManager: TempoVibrationsManager

    @Inject
    lateinit var currentExerciseRepository: CurrentExerciseRepository

    // Provides access to sensor data through [ExerciseClient].
    @Inject
    lateinit var healthManager: HealthServicesManager

    // For storing details of the workout.
    @Inject
    lateinit var savedExerciseDao: SavedExerciseDao

    // For creating a map at the end of the workout.
    @Inject
    lateinit var routeMapCreator: RouteMapCreator

    private val binder = LocalBinder()
    private var started = false

    var exerciseState = mutableStateOf(ExerciseState.ENDED)
        private set

    var currentSettings: MutableState<ExerciseSettingsWithScreens?> = mutableStateOf(null)
        private set

    val currentExercise = mutableStateOf(
        value = CurrentExercise(),
        policy = referentialEqualityPolicy()
    )

    var cacheFileId: String? = null

    val checkpoint =
        mutableStateOf(ExerciseUpdate.ActiveDurationCheckpoint(Instant.now(), Duration.ZERO))

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (!started) {
            enableForegroundService()
            started = true

            lifecycleScope.launch(Dispatchers.IO) {
                healthManager.exerciseUpdateFlow.collect { message ->
                    if (currentExerciseRepository.hasCurrentExercise()) {
                        currentExercise.value =
                            currentExerciseRepository.updateFromExerciseMessage(message)
                    }
                    when (message) {
                        is ExerciseMessage.ExerciseUpdateMessage -> {
                            message.update.activeDurationCheckpoint?.let { checkpoint.value = it }

                            if (message.update.exerciseStateInfo.state.isEnded) {
                                // TODO handle different end reasons
                                if (currentExerciseRepository.hasCurrentExercise()) {
                                    saveExercise(currentExerciseRepository.getCurrentExercise())

                                }
                                currentExerciseRepository.disposeCurrentExercise()
                                stopSelf()
                            }
                            if (exerciseState.value != message.update.exerciseStateInfo.state) {
                                tempoVibrationsManager.vibrateByStateTransition(exerciseState.value)
                            }
                            exerciseState.value = message.update.exerciseStateInfo.state
                        }

                        else -> {}
                    }
                }
            }
        }
        Log.i(TAG, "service onStartCommand")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        maybeStopService()
        return true
    }

    fun prepare(settingsId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            currentSettings.value = tempoSettingsManager.getExerciseSettings(settingsId).first()
            currentSettings.value?.let {
                currentExerciseRepository.initializeCurrentExercise(it.displayMetricsSet)
            }
            healthManager.prepare(currentSettings.value!!)
        }
    }

    fun startExercise(settingsId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val settings = tempoSettingsManager.getExerciseSettings(settingsId).first()
            currentSettings.value = settings
            cacheFileId = healthManager.startExercise(currentSettings.value!!)
        }
    }

    fun endExercise() {
        lifecycleScope.launch(Dispatchers.IO) {
            healthManager.endExercise()
        }
    }

    fun pauseResumeExercise() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (exerciseState.value.isUserPaused) {
                healthManager.resumeExercise()
            } else if (!exerciseState.value.isAutoPauseState()) {
                healthManager.pauseExercise()
            }
        }
    }

    private fun maybeStopService() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (exerciseState.value == ExerciseState.PREPARING) {
                healthManager.endExercise()
                stopSelf()
            }
            if (exerciseState.value.isEnded) {
                stopSelf()
            }
        }
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
            // TODO add the status here, e.g. "Active", "Paused" etc
            .addTemplate(STATUS_TEMPLATE)
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

    private suspend fun saveExercise(currentExercise: CurrentExercise) {
        if (currentExercise.wasStarted) {
            val summaryMetrics =
                currentSettings.value?.exerciseSettings?.endSummaryMetrics ?: setOf()
            val finalMetrics = currentExercise.metricsMap.entries.filter {
                summaryMetrics.contains(it.key)
            }.map {
                SavedExerciseMetric(
                    metric = it.key,
                    longValue = if (it.value is Long) it.value.toLong() else null,
                    doubleValue = if (it.value is Double) it.value.toDouble() else null
                )
            }
            val savedExercise = SavedExercise(
                recordingId = currentExercise.id.toString(),
                startTime = currentExercise.startDateTime!!,
                activeDuration = checkpoint.value.activeDuration
            )
            val databaseId = savedExerciseDao.insert(savedExercise, finalMetrics)

            cacheFileId?.let { cacheId -> routeMapCreator.createMap(cacheId, databaseId) }
            cacheFileId = null
        }
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
