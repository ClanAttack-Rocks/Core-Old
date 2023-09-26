package rocks.clanattack.player.trait

import kotlin.reflect.KClass

/**
 * The [DataTrait] is used to store data for a player.
 *
 * Data is stored as key-value pairs where the key and value is always a [String].
 * However, the trait can map the value to a different type.
 */
interface DataTrait {

    /**
     * All data that is currently stored for the player.
     */
    val data: Map<String, String>

    /**
     * Checks if data with the given [key] exists.
     */
    operator fun contains(key: String): Boolean

    /**
     * Sets the value of a data entry.
     */
    operator fun set(key: String, value: Any)

    /**
     * Gets the value of a data entry and maps it to the given [type][T],
     * or `null` if no data entry with the given [key] exists.
     */
    operator fun <T : Any> get(key: String, type: KClass<T>): T?

    /**
     * Gets the value of a data entry and maps it to the given [type][T],
     * or the given [default] if no data entry with the given [key] exists.
     */
    operator fun <T : Any> get(key: String, type: KClass<T>, default: T): T

    /**
     * Removes a data entry.
     */
    fun remove(key: String)

}

/**
 * Gets the value of a data entry and maps it to the given [type][T],
 * or `null` if no data entry with the given [key] exists.
 */
inline fun <reified T : Any> DataTrait.get(key: String): T? = get(key, T::class)

/**
 * Gets the value of a data entry and maps it to the given [type][T],
 * or the given [default] if no data entry with the given [key] exists.
 */
inline fun <reified T : Any> DataTrait.get(key: String, default: T): T = get(key, T::class, default)
