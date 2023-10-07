package rocks.clanattack.impl.util.serialization.module.datetime.zone

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.TimeZone
import rocks.clanattack.util.extention.unit

class TimeZoneSerializer : StdSerializer<TimeZone>(TimeZone::class.java) {

    override fun serialize(value: TimeZone?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeString(value?.id ?: "UTC") }

}