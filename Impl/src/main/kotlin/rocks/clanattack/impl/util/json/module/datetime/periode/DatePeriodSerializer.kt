package rocks.clanattack.impl.util.json.module.datetime.periode

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.DatePeriod

class DatePeriodSerializer : StdSerializer<DatePeriod>(DatePeriod::class.java) {

    override fun serialize(value: DatePeriod?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeNumberField("years", value?.years ?: 0)
        gen?.writeNumberField("months", value?.months ?: 0)
        gen?.writeNumberField("days", value?.days ?: 0)
        gen?.writeEndObject()
    }

}