package rocks.clanattack.impl.util.json.module.datetime

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.Instant

class InstantDeserializer : StdDeserializer<Instant>(Instant::class.java) {

    override fun deserialize(p: JsonParser?, context: DeserializationContext?) =
        p?.codec?.readValue(p, Long::class.java)?.let { Instant.fromEpochMilliseconds(it) }

}