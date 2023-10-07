package rocks.clanattack.impl.util.serialization.module.datetime.periode

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.DatePeriod

class DatePeriodDeserializer : StdDeserializer<DatePeriod>(DatePeriod::class.java) {

    @Suppress("DuplicatedCode")
    override fun deserialize(p: JsonParser?, context: DeserializationContext?): DatePeriod {
        val node = p?.codec?.readTree<JsonNode>(p)
        if (node?.isObject != true) throw IllegalArgumentException("Expected object node")

        val years = node.get("years").asInt()
        val months = node.get("months").asInt()
        val days = node.get("days").asInt()

        return DatePeriod(years, months, days)
    }

}