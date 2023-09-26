package rocks.clanattack.setting.event

import org.bukkit.event.Event

/**
 * This event is called when a setting is updated.
 *
 * When listing to it, `includeSubevents` must be set to `true`.
 *
 * @property key The key of the setting that was updated.
 * @property oldValue The old value of the setting.
 * @property newValue The new value of the setting.
 */
abstract class PluginSettingUpdatedEvent(val key: String, val oldValue: String, val newValue: String) : Event() {

    /**
     * Gets the old value of the setting and maps it to the given [type][T].
     */
    abstract fun <T : Any> getOldValue(type: Class<T>): T

    /**
     * Gets the new value of the setting and maps it to the given [type][T].
     */
    abstract fun <T : Any> getNewValue(type: Class<T>): T

}

/**
 * Gets the old value of the setting and maps it to the given [type][T].
 */
inline fun <reified T : Any> PluginSettingUpdatedEvent.getOldValue() = getOldValue(T::class.java)

/**
 * Gets the new value of the setting and maps it to the given [type][T].
 */
inline fun <reified T : Any> PluginSettingUpdatedEvent.getNewValue() = getNewValue(T::class.java)