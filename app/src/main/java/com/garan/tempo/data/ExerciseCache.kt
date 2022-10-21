//package com.garan.tempo.data
//
//import android.content.Context
//import android.os.SystemClock
//import android.util.Log
//import androidx.health.services.client.data.Availability
//import androidx.health.services.client.data.DataPoint
//import androidx.health.services.client.data.DataPoints
//import androidx.health.services.client.data.DataType
//import androidx.health.services.client.data.DataTypeAvailability
//import androidx.health.services.client.data.ExerciseState
//import androidx.health.services.client.data.ExerciseType
//import androidx.health.services.client.data.ExerciseUpdate
//import androidx.health.services.client.data.LocationAccuracy
//import androidx.health.services.client.proto.DataProto
//import com.garan.tempo.TAG
//import com.garmin.fit.Activity
//import com.garmin.fit.ActivityMesg
//import com.garmin.fit.DateTime
//import com.garmin.fit.DeviceInfoMesg
//import com.garmin.fit.Event
//import com.garmin.fit.EventMesg
//import com.garmin.fit.EventType
//import com.garmin.fit.FileEncoder
//import com.garmin.fit.FileIdMesg
//import com.garmin.fit.Fit
//import com.garmin.fit.LapMesg
//import com.garmin.fit.LapTrigger
//import com.garmin.fit.Manufacturer
//import com.garmin.fit.RecordMesg
//import com.garmin.fit.SessionMesg
//import com.garmin.fit.SourceType
//import com.garmin.fit.Sport
//import com.garmin.fit.SubSport
//import com.garmin.fit.TimerTrigger
//import java.io.File
//import java.time.Duration
//import java.time.Instant
//import java.time.ZonedDateTime
//import kotlin.math.pow
//import kotlin.math.roundToInt
//
//class ExerciseCache(
//    private val context: Context,
//    private val exerciseType: ExerciseType,
//    private val exerciseId: String
//    ) {
//    private val fitTimeOffsetSeconds = 631065600L
//    private val bootInstant =
//        Instant.ofEpochMilli(System.currentTimeMillis() - SystemClock.elapsedRealtime())
//    private val currentOffset = ZonedDateTime.now().offset
//    private val latIndex = DataPoints.LOCATION_DATA_POINT_LATITUDE_INDEX
//    private val lngIndex = DataPoints.LOCATION_DATA_POINT_LONGITUDE_INDEX
//    private val altIndex = DataPoints.LOCATION_DATA_POINT_ALTITUDE_INDEX
//
//    class OnChangeHeartRate {
//        var heartRateAvailability: DataTypeAvailability = DataTypeAvailability.UNKNOWN
//        var lastHeartRate: Short = 0
//    }
//    private val onChangeHeartRate = OnChangeHeartRate()
//
//    private var exerciseHasStarted = false
//    private var lastState: ExerciseState = ExerciseState.USER_ENDED
//    private var startInstant : Instant? = null
//
//    private val fitRecordList = mutableListOf<RecordMesg>()
//    private val fitEventList = mutableListOf<EventMesg>()
//
//    private val fitMesgList = mutableListOf(
//        buildFileIdMesg(),
//        buildDeviceInfoMesg()
//    )
//
//    private val cache = TimeRecordCache(
//        processor = { records ->
//            fitRecordList.addAll(records)
//        },
//        onChangeHeartRate = onChangeHeartRate
//    )
//
//    fun processUpdate(exerciseUpdate: ExerciseUpdate) {
//        DataProto.ExerciseUpdate.parseFrom()
//
//        if (exerciseHasStarted && !exerciseUpdate.state.isPaused) {
//            processLatestMetrics(exerciseUpdate.latestMetrics)
//        }
//        // When the exercise state changes, e.g. from active to paused, for some changes a
//        // [EventMesg] message should be written to capture that change of state.
//        if (lastState != exerciseUpdate.state) {
//            processStateChange(exerciseUpdate)
//        }
//        lastState = exerciseUpdate.state
//    }
//
//    fun processAvailability(dataType: DataType, availability: Availability) {
//        if (dataType == DataType.HEART_RATE_BPM && availability is DataTypeAvailability) {
//            onChangeHeartRate.heartRateAvailability = availability
//        }
//    }
//
//    private fun processLatestMetrics(latestMetrics: Map<DataType, List<DataPoint>>) {
//        latestMetrics.entries.forEach { entry ->
//            val dataType = entry.key
//            entry.value.forEach { dataPoint ->
//                val timestamp = dataPoint.fitDateTimeSecond
//                val record = cache.getOrCreate(timestamp)
//                when (dataType) {
//                    DataType.LOCATION -> {
//                        val array = dataPoint.value.asDoubleArray()
//                        record.positionLat = array[latIndex].semicircles
//                        record.positionLong = array[lngIndex].semicircles
//                        if (array.size > altIndex && array[altIndex] != Double.MAX_VALUE) {
//                            // TODO: Look at scaling - need to check this altitude is correct.
//                            record.altitude = array[altIndex].toFloat()
//                        }
//                        dataPoint.accuracy?.let {
//                            record.gpsAccuracy =
//                                (it as LocationAccuracy).horizontalPositionError.toInt().toShort()
//                        }
//                    }
//                    DataType.HEART_RATE_BPM -> {
//                        if (onChangeHeartRate.heartRateAvailability == DataTypeAvailability.AVAILABLE) {
//                            record.heartRate = dataPoint.value.asDouble().toInt().toShort()
//                            onChangeHeartRate.lastHeartRate = record.heartRate
//                        }
//                    }
//                }
//            }
//        }
//        cache.flush()
//    }
//
//    private fun processStateChange(exerciseUpdate: ExerciseUpdate) {
//        // For the majority of events, e.g. pausing, resuming, stopping, the timestamp is taken as
//        // that of right now. For the initial start however, Health Services provides that timestamp
//        // so that is used in preference.
//        var nowInstant = Instant.now()
//        if (!exerciseHasStarted && exerciseUpdate.state == ExerciseState.ACTIVE) {
//            // The global [startInstant] is used in a variety of messages within the FIT file.
//            startInstant = exerciseUpdate.startTime
//            exerciseHasStarted = true
//            nowInstant = startInstant
//        }
//
//        buildEventMessage(exerciseUpdate.state, nowInstant)?.let {
//            fitEventList.add(it)
//        }
//
//        if (exerciseHasStarted && exerciseUpdate.state.isEnded) {
//            cache.finalize()
//            fitMesgList.addAll(fitRecordList)
//            fitMesgList.addAll(fitEventList)
//            fitMesgList.add(buildLapMesg(nowInstant))
//            fitMesgList.add(buildSessionMesg(nowInstant, exerciseUpdate))
//            fitMesgList.add(buildActivityMesg(nowInstant, exerciseUpdate))
//            finalizeFile()
//        }
//    }
//
//    fun finalizeFile() {
//        val path = context.filesDir.absolutePath + "/$exerciseId.fit"
//        val encoder = FileEncoder(File(path), Fit.ProtocolVersion.V2_0)
//        encoder.write(fitMesgList)
//        encoder.close()
//    }
//
//    private fun buildEventMessage(
//        state: ExerciseState,
//        instant: Instant
//    ): EventMesg? {
//        val type = when (state) {
//            ExerciseState.ACTIVE -> EventType.START
//            ExerciseState.USER_PAUSED,
//            ExerciseState.AUTO_PAUSED -> EventType.STOP_ALL
//            ExerciseState.USER_ENDED,
//            ExerciseState.AUTO_ENDED,
//            ExerciseState.AUTO_ENDED_PERMISSION_LOST,
//            ExerciseState.TERMINATED -> EventType.STOP_ALL
//            else -> null
//        }
//        type?.let {
//            return EventMesg().apply {
//                timerTrigger = if (state.name.startsWith("AUTO")) {
//                    TimerTrigger.AUTO
//                } else {
//                    TimerTrigger.MANUAL
//                }
//                eventType = type
//                event = Event.TIMER
//                eventGroup = 0
//                timestamp = DateTime(instant.fitDateTimeSecond)
//            }
//        }
//        return null
//    }
//
//    private fun buildLapMesg(endInstant: Instant) = LapMesg().apply {
//        timestamp = DateTime(endInstant.fitDateTimeSecond)
//        startTime = DateTime(startInstant!!.fitDateTimeSecond)
//        messageIndex = 1
//        lapTrigger = LapTrigger.SESSION_END
//        event = Event.LAP
//        eventType = EventType.STOP
//    }
//
//    private fun buildSessionMesg(endInstant: Instant, lastUpdate: ExerciseUpdate) = SessionMesg().apply {
//        timestamp = DateTime(endInstant.fitDateTimeSecond)
//        totalTimerTime = lastUpdate.activeDuration.seconds.toFloat()
//        totalElapsedTime = Duration.between(startInstant, endInstant).seconds.toFloat()
//        timestamp = DateTime(endInstant.fitDateTimeSecond)
//        val (fitSport, fitSubSport) = exerciseType.toFitSports()
//        sport = fitSport
//        fitSubSport?.let { subSport = it }
//        messageIndex = 0
//        firstLapIndex = 0
//        numLaps = 1
//    }
//
//    private fun buildActivityMesg(endInstant: Instant, lastUpdate: ExerciseUpdate) = ActivityMesg().apply {
//        timestamp = DateTime(endInstant.fitDateTimeSecond)
//        numSessions = 1
//        localTimestamp = timestamp.timestamp + currentOffset.totalSeconds
//        totalTimerTime = lastUpdate.activeDuration.seconds.toFloat()
//        type = Activity.MANUAL
//        event = Event.ACTIVITY
//        eventType = EventType.STOP
//    }
//
//    private fun buildFileIdMesg() = FileIdMesg().apply {
//        manufacturer = Manufacturer.DEVELOPMENT
//        type = com.garmin.fit.File.ACTIVITY
//        product = 1
//        serialNumber = 1234
//        timeCreated = DateTime(Instant.now().fitDateTimeSecond)
//    }
//
//    private fun buildDeviceInfoMesg() = DeviceInfoMesg().apply {
//        timestamp = DateTime(Instant.now().fitDateTimeSecond)
//        this.deviceIndex = 0
//        this.sourceType = SourceType.LOCAL
//        this.serialNumber = 1234L
//        this.manufacturer = Manufacturer.DEVELOPMENT
//        this.product = 1
//        this.productName = "MyExport"
//        this.softwareVersion = 1f
//    }
//
//    private val DataPoint.fitDateTimeSecond: Long
//        get() = this.getEndInstant(bootInstant).fitDateTimeSecond
//
//    private val Instant.fitDateTimeSecond: Long
//        get() = this.minusSeconds(fitTimeOffsetSeconds).epochSecond
//
//    // Lat and long in FIT files is represented in the "semicircles" unit:
//    // https://gis.stackexchange.com/questions/156887/conversion-between-semicircles-and-latitude-units
//    private val Double.semicircles: Int
//        get() = ((this * 2.0.pow(31.0)) / 180.0).toInt()
//
//    private fun ExerciseType.toFitSports(): Pair<Sport, SubSport?> = when (this) {
//        ExerciseType.UNKNOWN -> Sport.GENERIC to SubSport.GENERIC
//        ExerciseType.BASKETBALL -> Sport.BASKETBALL to null
//        ExerciseType.BIKING -> Sport.CYCLING to null
//        ExerciseType.BIKING_STATIONARY -> Sport.CYCLING to SubSport.INDOOR_CYCLING
//        ExerciseType.RUNNING -> Sport.RUNNING to SubSport.GENERIC
//        ExerciseType.WALKING -> Sport.WALKING to SubSport.GENERIC
//        // etc
//        else -> Sport.GENERIC to null
//    }
//}