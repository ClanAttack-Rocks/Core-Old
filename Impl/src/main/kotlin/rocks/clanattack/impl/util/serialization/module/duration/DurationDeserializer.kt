package rocks.clanattack.impl.util.serialization.module.duration

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

class DurationDeserializer : StdDeserializer<Duration>(Duration::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readValue(p, Long::class.java)?.nanoseconds

}