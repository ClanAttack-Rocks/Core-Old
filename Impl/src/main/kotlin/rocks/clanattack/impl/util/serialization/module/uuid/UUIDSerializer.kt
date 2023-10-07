package rocks.clanattack.impl.util.serialization.module.uuid

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import rocks.clanattack.util.extention.unit
import java.util.*

class UUIDSerializer : StdSerializer<UUID>(UUID::class.java) {

    override fun serialize(value: UUID?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeString(value?.toString()) }

}