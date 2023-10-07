package rocks.clanattack.impl.util.json.module.adventure

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import rocks.clanattack.util.extention.unit

class ComponentSerializer : StdSerializer<Component>(Component::class.java) {

    override fun serialize(value: Component?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeString(value?.let { JSONComponentSerializer.json().serialize(value) }) }

}