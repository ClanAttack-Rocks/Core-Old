package rocks.clanattack.impl.util.serialization.module.duration

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import rocks.clanattack.util.extention.unit
import kotlin.time.Duration

class DurationSerializer : StdSerializer<Duration>(Duration::class.java) {

    override fun serialize(value: Duration?, gen: JsonGenerator?, provider: SerializerProvider?) =
        unit { gen?.writeNumber(value?.inWholeNanoseconds ?: 0) }

}