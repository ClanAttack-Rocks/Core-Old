package rocks.clanattack.setting

import rocks.clanattack.entry.service.Service
import kotlin.reflect.KClass

/**
 * The [SettingService] is used to store settings in the database.
 *
 * Settings are stored as key-value paris where the key and value is always a [String].
 * However, the service can map the value to a different type.
 *
 * The keys are case-sensitive.
 */
@Suppress("unused")
interface SettingService : Service {

    /**
     * All settings that are currently stored in the database.
     */
    val settings: Map<String, String>

    /**
     * Registers a setting.
     *
     * This will only create the setting if no setting with the given [key] exists.
     */
    fun registerSetting(key: String, value: Any)

    /**
     * Sets the value of a setting.
     *
     * This will create the setting if no setting with the given [key] exists.
     */
    operator fun set(key: String, value: Any)

    /**
     * Checks if a setting with the given [key] exists.
     */
    operator fun contains(key: String): Boolean

    /**
     * Gets the value of a setting and maps it to the given [type][T].
     *
     * Returns `null` if no setting with the given [key] exists.
     */
    operator fun <T : Any> get(key: String, type: KClass<T>): T?

    /**
     * Gets the value of a setting and maps it to the given [type][T].
     *
     * Returns the given [default] if no setting with the given [key] exists.
     */
    operator fun <T : Any> get(key: String, type: KClass<T>, default: T): T

}


/**
 * Gets the value of a setting and maps it to the given [type][T].
 *
 * Returns `null` if no setting with the given [key] exists.
 */
inline operator fun <reified T : Any> SettingService.get(key: String) = get(key, T::class)

/**
 * Gets the value of a setting and maps it to the given [type][T].
 *
 * Returns the given [default] if no setting with the given [key] exists.
 */
inline operator fun <reified T : Any> SettingService.get(key: String, default: T) = get(key, T::class, default)