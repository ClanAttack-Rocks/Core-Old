package rocks.clanattack.util.serialization

import rocks.clanattack.entry.service.Service
import kotlin.reflect.KClass
import com.fasterxml.jackson.databind.Module

/**
 * The [SerializationService] can be used to serialize any object to a [String] and deserialize it back.
 */
interface SerializationService : Service {

    /**
     * Serializes the given [value] to a [String].
     */
    fun serialize(value: Any): String

    /**
     * Deserializes the given [value] to an object of type [T].
     */
    fun <T : Any> deserialize(value: String, type: KClass<T>): T

    /**
     * Registers a jackson module.
     */
    fun registerModule(module: Module)

}