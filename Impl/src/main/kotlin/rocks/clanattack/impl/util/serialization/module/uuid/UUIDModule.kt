package rocks.clanattack.impl.util.serialization.module.uuid

import com.fasterxml.jackson.databind.module.SimpleModule
import java.util.UUID

object UUIDModule : SimpleModule() {

    init {
        addSerializer(UUID::class.java, UUIDSerializer())
        addDeserializer(UUID::class.java, UUIDDeserializer())
    }

    private fun readResolve(): Any = UUIDModule

}