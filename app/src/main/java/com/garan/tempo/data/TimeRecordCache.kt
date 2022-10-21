//package com.garan.tempo.data
//
//import androidx.health.services.client.data.DataTypeAvailability
//import com.garmin.fit.DateTime
//import com.garmin.fit.RecordMesg
//import java.lang.Long.max
//import java.util.PriorityQueue
//import kotlin.math.sign
//
///**
// * A cache for creating [RecordMesg] objects for given timestamps, taken from a generally increasing
// * stream of timestamps (but order not completely guaranteed), and keeping the object accessible for
// * update, until such point that they are aged, deemed "complete" and can then be flushed out in a
// * strictly increasing order.
// */
//class TimeRecordCache(
//    private val maxTimeAgeSeconds: Long = 4,
//    val processor: (List<RecordMesg>) -> Unit,
//    val onChangeHeartRate: ExerciseCache.OnChangeHeartRate
//) {
//    private class TimeRecord(val timestamp: Long, val record: RecordMesg)
//
//    // The most recent timestamp observed - used to determine which records can be aged out.
//    private var maxTimestamp = 0L
//    private val trQueue = PriorityQueue<TimeRecord> { a, b -> (a.timestamp - b.timestamp).sign }
//    private val trMap = mutableMapOf<Long, TimeRecord>()
//
//    /**
//     * Obtains the [RecordMesg] for the specified timestamp, or if it doesn't exist, creates it
//     * first.
//     */
//    fun getOrCreate(timestamp: Long): RecordMesg {
//        maxTimestamp = max(maxTimestamp, timestamp)
//        val tr = trMap.getOrPut(timestamp) {
//            val newEntry = TimeRecord(
//                timestamp = timestamp,
//                record = RecordMesg().apply {
//                    this.timestamp = DateTime(timestamp)
//                    // Heart rate is handled as a special case: It's an on-change sensor (or should
//                    // be) so the sensor may not emit a value, but the record should still contain
//                    // one if the current value is known, hasn't changed, and the sensor still
//                    // available. So it is initialized here, though may be updated through an
//                    // incoming value.
//                    if (onChangeHeartRate.heartRateAvailability == DataTypeAvailability.AVAILABLE) {
//                        this.heartRate = onChangeHeartRate.lastHeartRate
//                    }
//                }
//            )
//            trQueue.add(newEntry)
//            newEntry
//        }
//        return tr.record
//    }
//
//    /**
//     * Should be called regularly, after a block of adding records that could be largely out of
//     * order (e.g. if adding all the lists of data points from [ExerciseUpdate#latestMetrics].
//     *
//     * Any records that are older than the most recent record by greater than the max age limit
//     * are flushed in ascending time order.
//     */
//    fun flush() {
//        val agedRecords = mutableListOf<RecordMesg>()
//        while (!trQueue.isEmpty() && maxTimestamp - trQueue.peek().timestamp > maxTimeAgeSeconds) {
//            val head = trQueue.poll()
//            agedRecords.add(head.record)
//            trMap.remove(head.timestamp)
//        }
//        processor(agedRecords)
//    }
//
//    /**
//     * Flushes out any remaining records in ascending time order. Must be called when the cache is
//     * being cleaned up.
//     */
//    fun finalize() {
//        val agedRecords = mutableListOf<RecordMesg>()
//        while (!trQueue.isEmpty()) {
//            val head = trQueue.poll()
//            agedRecords.add(head.record)
//            trMap.remove(head.timestamp)
//        }
//        processor(agedRecords)
//    }
//}