package rocks.clanattack.impl.util.serialization.module.adventure

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import net.kyori.adventure.text.format.TextColor

class TextColorDeserializer : StdDeserializer<TextColor>(TextColor::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readValue(p, String::class.java)?.let { TextColor.fromHexString(it) }

}