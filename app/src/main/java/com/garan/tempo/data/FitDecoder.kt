package com.garan.tempo.data

import android.content.Context
import com.garmin.fit.Decode
import com.garmin.fit.FitRuntimeException
import com.garmin.fit.MesgBroadcaster
import com.garmin.fit.RecordMesg
import com.garmin.fit.RecordMesgListener


class FitDecoder(val context: Context) {
    class DecodeListener : RecordMesgListener {
        val latLongs = mutableListOf<Pair<Double, Double>>()

        override fun onMesg(record: RecordMesg?) {
            val lat = record?.getField("position_lat")
            val lng = record?.getField("position_long")
            if (lat != null && lng != null) {
                latLongs.add(
                    Pair(
                        lat.integerValue.degrees,
                        lng.integerValue.degrees
                    )
                )
            }
        }

        private val Int.degrees: Double
            get() = (this * 180.0) / Math.pow(2.0, 31.0)
    }

    fun latLongsForId(id: String): List<Pair<Double, Double>> {
        val decode = Decode()
        //decode.skipHeader();        // Use on streams with no header and footer (stream contains FIT defn and data messages only)
        //decode.incompleteStream();  // This suppresses exceptions with unexpected eof (also incorrect crc)
        val mesgBroadcaster = MesgBroadcaster(decode)
        val listener = DecodeListener()
        mesgBroadcaster.addListener(listener)

        context.openFileInput("$id.fit").use { inputStream ->
            try {
                mesgBroadcaster.run(inputStream)
            } catch (e: FitRuntimeException) {
                System.err.print("Exception decoding file: ")
                System.err.println(e.message)
                return listOf()
            }
        }

        return listener.latLongs
    }
}