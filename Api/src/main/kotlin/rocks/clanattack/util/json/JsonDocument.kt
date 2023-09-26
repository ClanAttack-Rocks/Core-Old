@file:Suppress("unused")

package rocks.clanattack.util.json

import java.io.File
import java.io.OutputStream
import kotlin.reflect.KClass

/**
 * A [JsonDocument] is a document, that can be serialized to a json string.
 */
interface JsonDocument {

    /**
     * The origin of this [JsonDocument].
     *
     * If this [JsonDocument] was gotten from another [JsonDocument], this will be the origin,
     * otherwise [origin] will be null.
     */
    val origin: JsonDocument?

    /**
     * The keys of this [JsonDocument].
     */
    val keys: List<String>

    /**
     * The size of this [JsonDocument].
     */
    val size: Int

    /**
     * Listens to changes of this [JsonDocument].
     */
    fun onChange(listener: (JsonDocument) -> Unit)

    /**
     * Removes a listener from this [JsonDocument].
     */
    fun removeListener(listener: (JsonDocument) -> Unit)

    /**
     * Prints this [JsonDocument] to the given [OutputStream].
     *
     * When [pretty] is true, the json will be pretty printed.
     */
    fun print(stream: OutputStream, pretty: Boolean = false)

    /**
     * Writes this [JsonDocument] to the given [File].
     *
     * When [pretty] is true, the json will be pretty printed.
     */
    fun write(file: File, pretty: Boolean = false)

    /**
     * Clones this [JsonDocument].
     *
     * This will remove all listeners from the cloned [JsonDocument],
     * and the [origin] of the cloned [JsonDocument] will be null.
     */
    fun clone(): JsonDocument

    /**
     * Flushes this [JsonDocument] to its origin.
     *
     * If this [JsonDocument] has no origin, this will do nothing.
     */
    fun flush()

    /**
     * Checks if this [JsonDocument] contains the given [key].
     */
    operator fun contains(key: String): Boolean

    /**
     * Gets the value of the given [key] as [T].
     *
     * @throws IllegalArgumentException if the value of the given [key] is not of type [T].
     */
    @Throws(IllegalArgumentException::class)
    operator fun <T : Any> get(key: String, type: KClass<T>): T?

    /**
     * Gets the value of the given [key] as [T] or returns [default] if the value is null.
     *
     * @throws IllegalArgumentException if the value of the given [key] is not of type [T].
     */
    @Throws(IllegalArgumentException::class)
    operator fun <T : Any> get(key: String, default: T, type: KClass<T>): T

    /**
     * Gets a list of the given [key] as [T].
     *
     * @throws IllegalArgumentException if the value of the given [key] is not a list of type [T].
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Any> getList(key: String, type: KClass<T>): List<T>?

    /**
     * Gets a list of the given [key] as [T] or returns [default] if the value is null.
     *
     * @throws IllegalArgumentException if the value of the given [key] is not a list of type [T].
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Any> getList(key: String, default: List<T>, type: KClass<T>): List<T>

    /**
     * Gets all values of the given [key] as [T].
     *
     * @throws IllegalArgumentException if the value of the given [key] is not a map with values of type [T].
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Any> getMap(key: String, type: KClass<T>): Map<String, T>?

    /**
     * Gets all values of the given [key] as [T] or returns [default] if the value is null.
     *
     * @throws IllegalArgumentException if the value of the given [key] is not a map with values of type [T].
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Any> getMap(key: String, default: Map<String, T>, type: KClass<T>): Map<String, T>

    /**
     * Sets the value of the given [key] to [value],
     * or removes the value if [value] is null.
     */
    operator fun set(key: String, value: Any?)

    /**
     * Clears this [JsonDocument].
     */
    fun clear()

    /**
     * Removes the value of the given [key].
     */
    fun remove(key: String)

    /**
     * Converts this [JsonDocument] to a json string.
     */
    fun toString(pretty: Boolean = false): String

}

/**
 * Gets the value of the given [key] as [T].
 *
 * @throws IllegalArgumentException if the value of the given [key] is not of type [T].
 */
@Throws(IllegalArgumentException::class)
inline fun <reified T : Any> JsonDocument.get(key: String) = this[key, T::class]

/**
 * Gets the value of the given [key] as [T] or returns [default] if the value is null.
 *
 * @throws IllegalArgumentException if the value of the given [key] is not of type [T].
 */
@Throws(IllegalArgumentException::class)
inline fun <reified T : Any> JsonDocument.get(key: String, default: T) = this[key, default, T::class]

/**
 * Gets a list of the given [key] as [T].
 *
 * @throws IllegalArgumentException if the value of the given [key] is not a list of type [T].
 */
inline fun <reified T : Any> JsonDocument.getList(key: String) = this.getList(key, T::class)

/**
 * Gets a list of the given [key] as [T] or returns [default] if the value is null.
 *
 * @throws IllegalArgumentException if the value of the given [key] is not a list of type [T].
 */
inline fun <reified T : Any> JsonDocument.getList(key: String, default: List<T>) = this.getList(key, default, T::class)

/**
 * Gets all values of the given [key] as [T].
 *
 * @throws IllegalArgumentException if the value of the given [key] is not a map with values of type [T].
 */
inline fun <reified T : Any> JsonDocument.getMap(key: String) = this.getMap(key, T::class)

/**
 * Gets all values of the given [key] as [T] or returns [default] if the value is null.
 *
 * @throws IllegalArgumentException if the value of the given [key] is not a map with values of type [T].
 */
inline fun <reified T : Any> JsonDocument.getMap(key: String, default: Map<String, T>) = this.getMap(key, default, T::class)
