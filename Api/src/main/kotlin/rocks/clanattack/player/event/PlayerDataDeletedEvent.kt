package rocks.clanattack.player.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import rocks.clanattack.player.Player

/**
 * This event is called when a player data entry is deleted.
 *
 * @property player The player whose data entry was deleted.
 * @property key The key of the data entry that was deleted.
 */
class PlayerDataDeletedEvent(val player: Player, val key: String) : Event() {

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