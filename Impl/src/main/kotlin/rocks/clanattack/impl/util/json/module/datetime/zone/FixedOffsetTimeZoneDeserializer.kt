package rocks.clanattack.impl.util.json.module.datetime.zone

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.FixedOffsetTimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import java.time.ZoneId

class FixedOffsetTimeZoneDeserializer : StdDeserializer<FixedOffsetTimeZone>(FixedOffsetTimeZone::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?): FixedOffsetTimeZone {
        val zone = TimeZone.of(
            p?.codec?.readValue(p, String::class.java)
                ?: throw IllegalArgumentException("Expected string")
        )

        if (zone !is FixedOffsetTimeZone) throw IllegalArgumentException("Expected fixed offset time zone")
        return zone
    }

}