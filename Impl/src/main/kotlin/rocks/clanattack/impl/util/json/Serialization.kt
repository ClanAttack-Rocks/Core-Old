package rocks.clanattack.impl.util.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import rocks.clanattack.entry.find
import rocks.clanattack.util.extention.unit
import java.util.UUID
import rocks.clanattack.util.json.JsonDocument as JsonDocumentInterface


class JsonDocumentSerializer : StdSerializer<JsonDocumentInterface>(JsonDocumentInterface::class.java) {

    override fun serialize(value: JsonDocumentInterface?, gen: JsonGenerator?, provider: SerializerProvider?) = unit {
        when (value) {
            null -> gen?.writeNull()
            is JsonDocument -> gen?.writeObject(value.data)
            else -> throw IllegalArgumentException("Cannot serialize ${value::class.java}")
        }
    }

}

class JsonDocumentDeserializer : StdDeserializer<JsonDocument>(JsonDocument::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readTree<ObjectNode>(p)?.let { find<JsonDocumentService>().fromNode(it) }

}

class UUIDSerializer : StdSerializer<UUID>(UUID::class.java) {

    override fun serialize(value: UUID?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeString(value?.toString()) }

}

class UUIDDeserializer : StdDeserializer<UUID>(UUID::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readValue(p, String::class.java)?.let {
            UUID.fromString(
                if (it.contains("-")) it
                else it.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(), "$1-$2-$3-$4-$5")
            )
        }

}

object JsonDocumentModule : SimpleModule() {

    init {
        addSerializer(JsonDocument::class.java, JsonDocumentSerializer())
        addDeserializer(JsonDocument::class.java, JsonDocumentDeserializer())

        addSerializer(JsonDocumentInterface::class.java, JsonDocumentSerializer())
        addDeserializer(JsonDocumentInterface::class.java, JsonDocumentDeserializer())

        addSerializer(UUID::class.java, UUIDSerializer())
        addDeserializer(UUID::class.java, UUIDDeserializer())
    }

    private fun readResolve(): Any = JsonDocumentModule

}