package com.garan.tempo.settings

import androidx.health.services.client.data.DataType
import androidx.room.TypeConverter
import com.garan.tempo.ui.metrics.DisplayMetric
import kotlin.reflect.typeOf

class Converters {
    private val dataTypeLookup = DataType.Companion::class.members.filter {
        it.returnType == typeOf<DataType>()
    }.map {
        val dataType = (it.call(this) as DataType)
        dataType.name to dataType
    }.toMap()

    @TypeConverter
    fun fromDataType(dataTypes: Set<DataType>): String =
        dataTypes.joinToString(
            separator = ",",
            transform = { dataType -> dataType.name }
        )

    @TypeConverter
    fun toDataType(encodedString: String): Set<DataType> {
        return encodedString.split(",").map {
            dataTypeLookup[it]!!
        }.toSet()
    }

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
