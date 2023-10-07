package rocks.clanattack.impl.util.serialization.module.datetime.datetime

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.LocalDateTime

class LocalDateTimeDeserializer : StdDeserializer<LocalDateTime>(LocalDateTime::class.java) {

    @Suppress("DuplicatedCode")
    override fun deserialize(p: JsonParser?, context: DeserializationContext?) : LocalDateTime {
        val node = p?.codec?.readTree<JsonNode>(p)
        if (node?.isObject != true) throw IllegalArgumentException("Expected object node")

        val year = node.get("year").asInt()
        val month = node.get("month").asInt()
        val day = node.get("day").asInt()
        val hour = node.get("hour").asInt()
        val minute = node.get("minute").asInt()
        val second = node.get("second").asInt()
        val nanosecond = node.get("nanosecond").asInt()

        return LocalDateTime(year, month, day, hour, minute, second, nanosecond)
    }

}