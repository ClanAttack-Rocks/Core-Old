package rocks.clanattack.impl.util.json.module.adventure

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import net.kyori.adventure.text.format.TextColor
import rocks.clanattack.util.extention.unit

class TextColorSerializer : StdSerializer<TextColor>(TextColor::class.java) {

    override fun serialize(value: TextColor?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeString(value?.asHexString()) }

}