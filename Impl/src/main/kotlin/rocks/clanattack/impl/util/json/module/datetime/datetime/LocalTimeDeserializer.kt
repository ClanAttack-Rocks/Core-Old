package rocks.clanattack.impl.util.json.module.datetime.datetime

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.LocalTime

class LocalTimeDeserializer : StdDeserializer<LocalTime>(LocalTime::class.java) {

    @Suppress("DuplicatedCode")
    override fun deserialize(p: JsonParser?, context: DeserializationContext?): LocalTime {
        val node = p?.codec?.readTree<JsonNode>(p)
        if (node?.isObject != true) throw IllegalArgumentException("Expected object node")

        val hour = node.get("hour").asInt()
        val minute = node.get("minute").asInt()
        val second = node.get("second").asInt()
        val nanosecond = node.get("nanosecond").asInt()

        return LocalTime(hour, minute, second, nanosecond)
    }

}