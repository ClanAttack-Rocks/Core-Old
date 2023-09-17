package rocks.clanattack.impl.util.minecraft.listener

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

class PlayerLoginListener : Listener {

    @EventHandler
    fun playerJoin(event: PlayerLoginEvent) {
        if (ListenerHandler.loaded) return

        event.disallow(
            PlayerLoginEvent.Result.KICK_OTHER,
            MiniMessage.miniMessage().deserialize(
                "<dark_gray>--------------- <#22d3ee>ClanAttack</#22d3ee> ---------------</dark_gray><newline>" +
                        "<newline>" +
                        "<gray>The System is currently </gray><#0284c7>booting</#0284c7><dark_gray>.</dark_gray><newline>" +
                        "<gray>Please try again in a </gray><#0284c7>few seconds</#0284c7><dark_gray>.</dark_gray><newline>" +
                        "<newline>" +
                        "<dark_gray>----------------- <gray>Rocks</gray> -----------------</dark_gray>"
            )
        )
    }

}