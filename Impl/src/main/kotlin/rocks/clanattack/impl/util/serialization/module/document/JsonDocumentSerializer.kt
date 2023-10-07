package rocks.clanattack.impl.util.serialization.module.document

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import rocks.clanattack.util.extention.unit
import rocks.clanattack.util.json.JsonDocument

class JsonDocumentSerializer : StdSerializer<JsonDocument>(JsonDocument::class.java) {

    override fun serialize(value: JsonDocument?, gen: JsonGenerator?, provider: SerializerProvider?) = unit {
        when (value) {
            null -> gen?.writeNull()
            is rocks.clanattack.impl.util.json.JsonDocument -> gen?.writeObject(value.data)
            else -> throw IllegalArgumentException("Cannot serialize ${value::class.java}")
        }
    }

}