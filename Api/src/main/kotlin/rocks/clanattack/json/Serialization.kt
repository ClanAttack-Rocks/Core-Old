package rocks.clanattack.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import rocks.clanattack.extention.unit

/**
 * The serializer to serialize a [JsonDocument] to a JSON string
 */
class JsonDocumentSerializer : StdSerializer<JsonDocument>(JsonDocument::class.java) {

    /**
     * Serialize the [JsonDocument] to a JSON string
     *
     * @param value the [JsonDocument] to serialize
     * @param gen the [JsonGenerator] to use
     * @param provider the [SerializerProvider] to use
     */
    override fun serialize(value: JsonDocument?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeObject(value?.map) }

}

/**
 * The deserializer to deserialize a [JsonDocument] from a JSON string
 */
class JsonDocumentDeserializer : StdDeserializer<JsonDocument>(JsonDocument::class.java) {

    /**
     * Deserialize the [JsonDocument] from a JSON string
     *
     * @param p the [JsonParser] to use
     * @param ctxt the [DeserializationContext] to use
     * @return the deserialized [JsonDocument]
     */
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?)=
        JsonDocument.fromNode(p?.codec?.readTree<ObjectNode>(p))

}

/**
 * The [JsonDocumentModule] provides the [JsonDocumentSerializer] and [JsonDocumentDeserializer] to the [SimpleModule]
 */
object JsonDocumentModule : SimpleModule() {

    init {
        addSerializer(JsonDocument::class.java, JsonDocumentSerializer())
        addDeserializer(JsonDocument::class.java, JsonDocumentDeserializer())
    }

}