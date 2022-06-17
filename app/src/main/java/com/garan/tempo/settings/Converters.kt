package com.garan.tempo.settings

import android.os.Parcel
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ProtoParcelable
import androidx.health.services.client.data.Value
import androidx.health.services.client.proto.DataProto
import androidx.room.TypeConverter
import com.garan.tempo.ui.metrics.DisplayMetric
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.reflect.jvm.internal.impl.protobuf.MessageLite
import kotlin.reflect.typeOf

class Converters {
    private val dataTypeLookup = DataType.Companion::class.members.filter {
        it.returnType == typeOf<DataType>()
    }.map {
        val dataType = (it.call(this) as DataType)
        dataType.name to dataType
    }.toMap()

    @TypeConverter
    fun fromValue(value: Value) : ByteArray = value.proto.toByteArray()

    @TypeConverter
    fun toValue(byteArray: ByteArray) : Value {
        val parcel = Parcel.obtain()
        parcel.writeByteArray(byteArray)
        parcel.setDataPosition(0)
        val value = Value.CREATOR.createFromParcel(parcel)
        parcel.recycle()
        return value
    }

    @TypeConverter
    fun fromDuration(duration: Duration): Long = duration.toMillis()

    @TypeConverter
    fun toDuration(millis: Long): Duration = Duration.ofMillis(millis)

    @TypeConverter
    fun fromZonedDateTime(zonedDateTime: ZonedDateTime): String =
        zonedDateTime.toString()

    @TypeConverter
    fun toZonedDateTime(dateString: String): ZonedDateTime =
        ZonedDateTime.parse(dateString)

    @TypeConverter
    fun fromDataTypeSet(dataTypes: Set<DataType>): String =
        dataTypes.joinToString(
            separator = ",",
            transform = { dataType -> dataType.name }
        )

    @TypeConverter
    fun toDataTypeSet(encodedString: String): Set<DataType> {
        return encodedString.split(",").map {
            dataTypeLookup[it]!!
        }.toSet()
    }

    @TypeConverter
    fun fromDataType(dataType: DataType): String = dataType.name

    @TypeConverter
    fun toDataType(value: String): DataType = dataTypeLookup[value]!!

    @TypeConverter
    fun fromDisplayMetrics(displayMetrics: List<DisplayMetric>): String =
        displayMetrics.joinToString(
            separator = ",",
            transform = { it.name }
        )

    @TypeConverter
    fun toDisplayMetrics(encodedString: String): List<DisplayMetric> {
        return encodedString.split(",").map {
            DisplayMetric.valueOf(it)
        }
    }
}
