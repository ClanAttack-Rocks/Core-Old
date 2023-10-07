package rocks.clanattack.impl.util.json.module.document

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import rocks.clanattack.entry.find
import rocks.clanattack.impl.util.json.JsonDocument
import rocks.clanattack.impl.util.json.JsonDocumentService

class JsonDocumentDeserializer : StdDeserializer<JsonDocument>(JsonDocument::class.java) {
    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readTree<ObjectNode>(p)?.let { find<JsonDocumentService>().fromNode(it) }

}