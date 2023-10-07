package rocks.clanattack.impl.util.serialization.module.document

import com.fasterxml.jackson.databind.module.SimpleModule
import rocks.clanattack.util.json.JsonDocument

object JsonDocumentModule : SimpleModule() {

    init {
        addSerializer(JsonDocument::class.java, JsonDocumentSerializer())
        addDeserializer(JsonDocument::class.java, JsonDocumentDeserializer())
    }

    private fun readResolve(): Any = JsonDocumentModule

}