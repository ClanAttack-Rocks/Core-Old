package rocks.clanattack.player.event

import org.bukkit.event.Event
import rocks.clanattack.player.Player


/**
 * This event is called when a player data entry is updated.
 *
 * When listing to it, `includeSubevents` must be set to `true`.
 *
 * @property player The player whose data entry was updated.
 * @property key The key of the setting that was updated.
 * @property oldValue The old value of the setting.
 * @property newValue The new value of the setting.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class PlayerDataUpdatedEvent(
    val player: Player,
    val key: String,
    val oldValue: String,
    val newValue: String,
) : Event() {

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
@Suppress("unused")
inline fun <reified T : Any> PlayerDataUpdatedEvent.getOldValue() = getOldValue(T::class.java)

/**
 * Gets the new value of the setting and maps it to the given [type][T].
 */
@Suppress("unused")
inline fun <reified T : Any> PlayerDataUpdatedEvent.getNewValue() = getNewValue(T::class.java)