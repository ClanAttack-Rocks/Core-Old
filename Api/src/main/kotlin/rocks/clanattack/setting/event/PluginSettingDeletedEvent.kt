package rocks.clanattack.setting.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * This event is called when a setting is deleted.
 *
 * @property key The key of the setting that was deleted.
 */
@Suppress("unused")
class PluginSettingDeletedEvent(val key: String) : Event() {

    /**
     * Required by bukkit.
     */
    override fun getHandlers() = handlerList

    companion object {

        /**
         * Required by bukkit.
         */
       @JvmStatic
       val handlerList = HandlerList()

    }

}