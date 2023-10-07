package rocks.clanattack.impl.util.json.module.adventure

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer

class ComponentDeserializer : StdDeserializer<Component>(Component::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readValue(p, String::class.java)?.let { JSONComponentSerializer.json().deserialize(it) }

}