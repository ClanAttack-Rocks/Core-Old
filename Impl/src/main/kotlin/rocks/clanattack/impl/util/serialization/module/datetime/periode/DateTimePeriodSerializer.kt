package rocks.clanattack.impl.util.serialization.module.datetime.periode

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.DateTimePeriod

class DateTimePeriodSerializer : StdSerializer<DateTimePeriod>(DateTimePeriod::class.java) {

    @Suppress("DuplicatedCode")
    override fun serialize(value: DateTimePeriod?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeNumberField("years", value?.years ?: 0)
        gen?.writeNumberField("months", value?.months ?: 0)
        gen?.writeNumberField("days", value?.days ?: 0)
        gen?.writeNumberField("hours", value?.hours ?: 0)
        gen?.writeNumberField("minutes", value?.minutes ?: 0)
        gen?.writeNumberField("seconds", value?.seconds ?: 0)
        gen?.writeNumberField("nanoseconds", value?.nanoseconds ?: 0)
        gen?.writeEndObject()
    }

}