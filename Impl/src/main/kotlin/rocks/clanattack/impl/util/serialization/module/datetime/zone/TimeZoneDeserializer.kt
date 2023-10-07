package rocks.clanattack.impl.util.serialization.module.datetime.zone

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.TimeZone

class TimeZoneDeserializer : StdDeserializer<TimeZone>(TimeZone::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readValue(p, String::class.java)?.let { TimeZone.of(it) }

}