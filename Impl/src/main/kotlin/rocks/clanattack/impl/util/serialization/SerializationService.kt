package rocks.clanattack.impl.util.serialization

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.util.serialization.module.adventure.AdventureModule
import rocks.clanattack.impl.util.serialization.module.datetime.DateTimeModule
import rocks.clanattack.impl.util.serialization.module.document.JsonDocumentModule
import rocks.clanattack.impl.util.serialization.module.duration.DurationModule
import rocks.clanattack.impl.util.serialization.module.uuid.UUIDModule
import kotlin.reflect.KClass
import rocks.clanattack.util.serialization.SerializationService as Interface

@Register(definition = Interface::class)
class SerializationService : ServiceImplementation(), Interface {

    var mapper: ObjectMapper
        private set

    init {
        mapper = ObjectMapper().registerModules(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build(),
            AdventureModule,
            DateTimeModule,
            JsonDocumentModule,
            DurationModule,
            UUIDModule,
        ).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    override fun serialize(value: Any): String = mapper.writeValueAsString(value)

    override fun <T : Any> deserialize(value: String, type: KClass<T>): T =
        mapper.readValue(value, type.java)

    override fun registerModule(module: Module) {
        mapper = mapper.registerModule(module)
    }

}