package com.garan.tempo.settings

import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import com.garan.tempo.ui.metrics.AggregationType
import com.garan.tempo.ui.metrics.DisplayMetric
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.util.UUID


//@Serializable
//data class ExerciseTypeMap(
//    val exercises: Map<ExerciseType, ExerciseSettings>
//)

@Serializable
data class ExerciseSettings(
    val name: String,
    val exerciseType: ExerciseType,
    val recordingMetrics: Set<@Serializable(with = DataTypeSerializer::class) DataType> = setOf(),
    val screens: List<ScreenSettings> = listOf(),
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
) {
    val displayMetricsSet = screens.flatMap { it.metrics }.toSet()

    fun getRequiredDataTypes() : Pair<Set<DataType>, Set<DataType>> {
        val dataTypes = recordingMetrics.toMutableSet()
        val aggregateDataTypes = mutableSetOf<DataType>()
        screens.forEach {
            it.metrics.forEach {
                when(it.aggregationType()) {
                    AggregationType.SAMPLE -> dataTypes.add(it.requiredDataType()!!)
                    AggregationType.AVG,
                    AggregationType.MIN,
                    AggregationType.MAX,
                    AggregationType.TOTAL -> aggregateDataTypes.add(it.requiredDataType()!!)
                    AggregationType.NONE -> {
                        // No aggregation for Active Duration DisplayMetric
                    }
                }
            }
        }
        return dataTypes to aggregateDataTypes
    }

    fun getRequiresGps() = recordingMetrics.contains(DataType.LOCATION)
}

@Serializable
data class ScreenSettings(
    val screenFormat: ScreenFormat = ScreenFormat.SIX_SLOT,
    val metrics: List<DisplayMetric> = listOf()
)

enum class ScreenFormat {
    ONE_SLOT,
    TWO_SLOT,
    ONE_PLUS_TWO_SLOT,
    ONE_PLUS_FOUR_SLOT,
    SIX_SLOT
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = DataType::class)
object DataTypeSerializer : KSerializer<DataType> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DataType") {
        element<String>("name")
        element<Int>("format")
        element<String>("timeType")
    }

    override fun serialize(encoder: Encoder, value: DataType) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeIntElement(descriptor, 1, value.format)
            encodeStringElement(descriptor, 2, value.timeType.name)
        }

    override fun deserialize(decoder: Decoder): DataType =
        decoder.decodeStructure(descriptor) {
            var name = ""
            var format = -1
            var timeType = ""
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, 0)
                    1 -> format = decodeIntElement(descriptor, 1)
                    2 -> timeType = decodeStringElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            require(name.isNotEmpty() and timeType.isNotEmpty() and (format >= 0))
            DataType(name, DataType.TimeType.valueOf(timeType), format)
        }
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

