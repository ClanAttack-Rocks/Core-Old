package rocks.clanattack.impl.json

import com.fasterxml.jackson.databind.JsonNode
import rocks.clanattack.entry.service.Register
import rocks.clanattack.extention.filterNotNull
import java.io.File
import java.io.InputStream
import rocks.clanattack.json.JsonDocument as JsonDocumentInterface
import rocks.clanattack.json.JsonDocumentService as Interface

@Register(definition = Interface::class)
class JsonDocumentService : Interface {

    override fun empty() = JsonDocument()

    override fun fromMap(map: Map<String, Any?>) = JsonDocument(map
        .filterNotNull()
        .mapValues { JsonDocument.mapper.valueToTree<JsonNode>(it.value) }
        .toMutableMap()
    )

    fun fromNode(node: JsonNode) =
        JsonDocument(node.fields().asSequence().associate { it.key to it.value }.toMutableMap())

    override fun fromObject(obj: Any) = fromNode(JsonDocument.mapper.valueToTree(obj))

    override fun fromString(string: String) = JsonDocument.mapper.readTree(string).let { fromNode(it) }

    override fun fromStream(stream: InputStream) = fromString(stream.bufferedReader().use { it.readText() })

    override fun fromFile(file: File) = fromString(file.readText())

    override fun fromBlock(block: JsonDocumentInterface.() -> Unit) = empty().apply(block)
}