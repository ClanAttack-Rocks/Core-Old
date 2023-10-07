package rocks.clanattack.impl.util.json.module.datetime.datetime

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.LocalDate

class LocalDateSerializer : StdSerializer<LocalDate>(LocalDate::class.java) {

    override fun serialize(value: LocalDate?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeNumberField("year", value?.year ?: 0)
        gen?.writeNumberField("month", value?.monthNumber ?: 0)
        gen?.writeNumberField("day", value?.dayOfMonth ?: 0)
        gen?.writeEndObject()
    }

}