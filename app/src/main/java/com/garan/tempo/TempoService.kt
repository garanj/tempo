package com.garan.tempo

//import com.garan.tempo.data.ExerciseCache
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.core.app.NotificationCompat
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.LocationAvailability
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.garan.tempo.data.AvailabilityHolder
import com.garan.tempo.data.SavedExerciseDao
import com.garan.tempo.mapping.RouteMapCreator
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.ui.metrics.MetricsRepository
import com.garan.tempo.ui.metrics.TempoMetric
import com.garan.tempo.vibrations.stateVibrations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.util.EnumMap
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class TempoService : LifecycleService() {
    // Settings that apply to the whole app, e.g. which units to use.
    @Inject
    lateinit var tempoSettingsManager: TempoSettingsManager

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

    private val vibrator by lazy { getSystemService(VIBRATOR_SERVICE) as Vibrator }

    private lateinit var currentWorkoutStart: ZonedDateTime

    //private var cache: ExerciseCache? = null
    // Maintain a status of datatypes where availability can vary. This is used to determine whether
    // the last held reading of for example heart rate, is still valid.
    val dataAvailability = mutableStateOf(AvailabilityHolder())

    var exerciseState = mutableStateOf(ExerciseState.ENDED)
        private set

    var currentSettings: MutableState<ExerciseSettingsWithScreens?> = mutableStateOf(null)
        private set

    var currentWorkoutId: MutableState<UUID?> = mutableStateOf(null)
        private set

    // The most recent metrics, for consumption in the UI.
    // Change this to an enum map
    //val metrics = DisplayUpdateMap()

    var metricsRepository = MetricsRepository()

    // The state of metrics is provided by an [EnumMap]. This is a useful representation as each
    // [TempoMetric] can be a key, and as that's an Enum, this can be efficiently represented, with
    // the UI retrieving only those that have been populated and subscribed to. On the downside,
    // there appears to be no immutable EnumMap, so this is a non-ideal use here, where the mutable
    // object is passed. Furthermore, the snapshot policy is set to never equal, as all updates from
    // a given WHS update are written to the EnumMap, before the map is then assigned to the metrics
    // value, to ensure that the UI only received an update once per WHS update (not for every time
    // the map is updated). There is probably a much better way to do this all, with something that
    // provides an immutable representation and a more meaningful snapshot policy.
    val metrics = mutableStateOf<EnumMap<TempoMetric, Number>>(
        value = EnumMap<TempoMetric, Number>(TempoMetric::class.java),
        policy = neverEqualPolicy()
    )

    val checkpoint = mutableStateOf<ExerciseUpdate.ActiveDurationCheckpoint?>(
        ExerciseUpdate.ActiveDurationCheckpoint(Instant.now(), Duration.ZERO)
    )

    // TODO - save latest aggregates
    private fun processExerciseUpdateForCache(exerciseUpdate: ExerciseUpdate) {
        //cache?.processUpdate(exerciseUpdate)
    }

    private fun processAvailability(message: ExerciseMessage.AvailabilityChangedMessage) {
        if (message.availability is LocationAvailability) {
            dataAvailability.value = dataAvailability.value.copy(
                locationAvailability = message.availability
            )
        } else if (message.availability is DataTypeAvailability
            && message.dataType == DataType.HEART_RATE_BPM
        ) {
            dataAvailability.value = dataAvailability.value.copy(
                heartRateAvailability = message.availability
            )
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
                            currentSettings.value?.let { current ->
                                //processExerciseUpdateForDisplay(message.update)

                                // Because [metrics] has a never equal snapshot policy, assigning it
                                // the EnumMap here serves to send an updated state to the UI. This
                                // is non-ideal, as really a snapshot policy should be used that has
                                // an efficient sense of equality for when a whole set of metrics
                                // have been added from WHS.
                                metrics.value = metricsRepository.processUpdate(message.update)
                                processExerciseUpdateForCache(message.update)

                                if (message.update.exerciseStateInfo.state.isEnded) {
                                    currentWorkoutId.value?.let { workoutId ->
                                        // TODO - move to a separate function
//                                        saveExercise(
//                                            startTime = currentWorkoutStart,
//                                            exerciseId = workoutId,
//                                            metrics = metrics,
//                                            // TODO - fix final duration
//                                            activeDuration = message.update.activeDurationCheckpoint!!
//                                        )
                                    }
                                }
                            }
                            if (exerciseState.value != message.update.exerciseStateInfo.state) {
                                checkpoint.value = message.update.activeDurationCheckpoint
                            }
                            exerciseState.value = message.update.exerciseStateInfo.state
                        }

                        is ExerciseMessage.AvailabilityChangedMessage -> {
                            //cache?.processAvailability(message.dataType, message.availability)
                            processAvailability(message)
                            metricsRepository.updateAvailability(dataAvailability.value)
                        }

                        else -> {

                        }
                    }
                }
            }
        }
        Log.i(TAG, "service onStartCommand")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.i(TAG, "service onBind")
        return binder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "service onRebind")
        // TODO - not working?
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "* service onUnbind")
        if (!exerciseState.value.isInProgress) {
            maybeStopService()
        }
        return true
    }

    fun prepare(settingsId: Int) {
        lifecycleScope.launch {
            currentSettings.value = tempoSettingsManager.getExerciseSettings(settingsId).first()
            healthManager.prepare(currentSettings.value!!)
        }
    }

    fun startExercise(settingsId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            currentSettings.value = tempoSettingsManager.getExerciseSettings(settingsId).first()
            currentWorkoutId.value = UUID.randomUUID()
            currentSettings.value?.let {
                metricsRepository = MetricsRepository(it.displayMetricsSet)
            }
//            cache = ExerciseCache(
//                context = this@TempoService,
//                exerciseType = currentSettings.value!!.exerciseSettings.exerciseType,
//                exerciseId = currentWorkoutId.value.toString()
//            )
            healthManager.startExercise(currentSettings.value!!)
            currentWorkoutStart = ZonedDateTime.now()

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
        stateVibrations[state]?.let {
            vibrator.vibrate(it)
        }
    }

    private fun maybeStopService() {
        lifecycleScope.launch {
            if (!exerciseState.value.isEnded) {
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
            // TODO
            //.addPart("duration", Status.TextPart("${}"))
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

//    private fun saveExercise(
//        startTime: ZonedDateTime,
//        exerciseId: UUID,
//        metrics: DisplayUpdateMap,
//        activeDuration: ExerciseUpdate.ActiveDurationCheckpoint
//    ) {
//        val savedExercise = SavedExercise(
//            exerciseId = exerciseId.toString(),
//            startTime = startTime,
//            activeDuration = activeDuration.activeDuration,
//            totalDistance = metrics.get(TempoMetric.DISTANCE)?.toDouble(),
//            totalCalories = metrics.get(TempoMetric.CALORIES)?.toDouble(),
//            avgPace = metrics.get(TempoMetric.AVG_PACE)?.toDouble(),
//            avgHeartRate = metrics.get(TempoMetric.AVG_HEART_RATE)?.toDouble(),
//        )
//        lifecycleScope.launch {
//            savedExerciseDao.insert(savedExercise)
//        }
//        routeMapCreator.createMap(exerciseId)
//    }

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
