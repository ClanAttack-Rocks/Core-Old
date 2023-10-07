package rocks.clanattack.impl.util.serialization.module.datetime

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.Instant
import rocks.clanattack.util.extention.unit

class InstantSerializer : StdSerializer<Instant>(Instant::class.java) {

    override fun serialize(value: Instant?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeNumber(value?.toEpochMilliseconds() ?: 0L) }

}