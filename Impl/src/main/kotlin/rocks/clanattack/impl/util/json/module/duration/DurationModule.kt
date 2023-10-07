package rocks.clanattack.impl.util.json.module.duration

import com.fasterxml.jackson.databind.module.SimpleModule
import kotlin.time.Duration

object DurationModule : SimpleModule() {

    init {
        addSerializer(Duration::class.java, DurationSerializer())
        addDeserializer(Duration::class.java, DurationDeserializer())
    }

    private fun readResolve(): Any = DurationModule

}