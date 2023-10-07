package rocks.clanattack.impl.util.serialization.module.datetime.datetime

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.LocalTime

class LocalTimeSerializer : StdSerializer<LocalTime>(LocalTime::class.java) {

    @Suppress("DuplicatedCode")
    override fun serialize(value: LocalTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeNumberField("hour", value?.hour ?: 0)
        gen?.writeNumberField("minute", value?.minute ?: 0)
        gen?.writeNumberField("second", value?.second ?: 0)
        gen?.writeNumberField("nanosecond", value?.nanosecond ?: 0)
        gen?.writeEndObject()
    }

}