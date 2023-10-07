package rocks.clanattack.impl.util.serialization.module.datetime.datetime

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.LocalDate

class LocalDateDeserializer : StdDeserializer<LocalDate>(LocalDate::class.java) {

    @Suppress("DuplicatedCode")
    override fun deserialize(p: JsonParser?, context: DeserializationContext?): LocalDate {
        val node = p?.codec?.readTree<JsonNode>(p)
        if (node?.isObject != true) throw IllegalArgumentException("Expected object node")

        val year = node.get("year").asInt()
        val month = node.get("month").asInt()
        val day = node.get("day").asInt()

        return LocalDate(year, month, day)
    }

}