package rocks.clanattack.impl.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import rocks.clanattack.entry.find
import rocks.clanattack.extention.unit

class JsonDocumentSerializer : StdSerializer<JsonDocument>(JsonDocument::class.java) {

    override fun serialize(value: JsonDocument?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeObject(value?.data) }

}

class JsonDocumentDeserializer : StdDeserializer<JsonDocument>(JsonDocument::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readTree<ObjectNode>(p)?.let { find<JsonDocumentService>().fromNode(it) }

}

object JsonDocumentModule : SimpleModule() {

    init {
        addSerializer(JsonDocument::class.java, JsonDocumentSerializer())
        addDeserializer(JsonDocument::class.java, JsonDocumentDeserializer())
    }

    private fun readResolve(): Any = JsonDocumentModule

}