package rocks.clanattack.impl.util.serialization.module.datetime.periode

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.DateTimePeriod

class DateTimePeriodDeserializer : StdDeserializer<DateTimePeriod>(DateTimePeriod::class.java) {

    @Suppress("DuplicatedCode")
    override fun deserialize(p: JsonParser?, context: DeserializationContext?): DateTimePeriod {
        val node = p?.codec?.readTree<JsonNode>(p)
        if (node?.isObject != true) throw IllegalArgumentException("Expected object node")

        val years = node.get("years").asInt()
        val months = node.get("months").asInt()
        val days = node.get("days").asInt()
        val hour = node.get("hours").asInt()
        val minute = node.get("minutes").asInt()
        val second = node.get("seconds").asInt()
        val nanoseconds = node.get("nanoseconds").asLong()

        return DateTimePeriod(years, months, days, hour, minute, second, nanoseconds)
    }

}