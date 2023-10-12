package rocks.clanattack.impl.minecraft.command

import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import rocks.clanattack.entry.find
import rocks.clanattack.minecraft.listener.Listen
import rocks.clanattack.util.extention.letIf
import rocks.clanattack.util.extention.rocksPlayer

object Listener {

    @Listen(PlayerCommandPreprocessEvent::class, includeSubevents = true)
    fun playerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.rocksPlayer
        val command = event.message.split(" ")[0]
            .letIf({ it.startsWith("/") }) { it.substring(1) }

        val whitelistedCommand = find<CommandManager>()
            .commands
            .firstOrNull {
                it.command.lowercase() == command.lowercase()
                        || it.aliases.any { alias -> alias.lowercase() == command.lowercase() }
            }

        if (whitelistedCommand == null) {
            player.communication.sendMessage("core.command.unknown")
            event.isCancelled = true
            return
        }

        if (whitelistedCommand.permission != "" && !player.hasPermission(whitelistedCommand.permission)) {
            player.communication.sendMessage("core.command.permission")
            event.isCancelled = true
            return
        }
    }

    @Listen(PlayerCommandSendEvent::class)
    fun playerCommandSend(event: PlayerCommandSendEvent) {
        event.commands.removeIf { command ->
            find<CommandManager>()
                .commands
                .none {
                    it.command.lowercase() == command.lowercase()
                            || it.aliases.any { alias -> alias.lowercase() == command.lowercase() }
                }
        }
    }

}