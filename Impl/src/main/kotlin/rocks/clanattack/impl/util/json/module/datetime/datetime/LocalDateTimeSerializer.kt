package rocks.clanattack.impl.util.json.module.datetime.datetime

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.LocalDateTime

class LocalDateTimeSerializer : StdSerializer<LocalDateTime>(LocalDateTime::class.java) {

    @Suppress("DuplicatedCode")
    override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeNumberField("year", value?.year ?: 0)
        gen?.writeNumberField("month", value?.monthNumber ?: 0)
        gen?.writeNumberField("day", value?.dayOfMonth ?: 0)
        gen?.writeNumberField("hour", value?.hour ?: 0)
        gen?.writeNumberField("minute", value?.minute ?: 0)
        gen?.writeNumberField("second", value?.second ?: 0)
        gen?.writeNumberField("nanosecond", value?.nanosecond ?: 0)
        gen?.writeEndObject()
    }

}