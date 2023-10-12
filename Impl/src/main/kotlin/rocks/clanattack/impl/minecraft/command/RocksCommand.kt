package rocks.clanattack.impl.minecraft.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import rocks.clanattack.entry.find
import rocks.clanattack.impl.player.PlayerService
import rocks.clanattack.impl.player.console.ConsolePlayer
import rocks.clanattack.java.MethodHelper
import java.lang.reflect.Method

class RocksCommand(
    name: String,
    aliases: List<String>,
    private val permission: String,
    private val executeMethod: Method,
    private val tabCompleteMethod: Method?,
) : Command(name, "", "", aliases) {

    init {
        setPermission(permission)
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>?): Boolean {
        if (!sender.hasPermission(permission)) return true

        val player = when (sender) {
            is Player -> find<PlayerService>()[sender.uniqueId]
            else -> ConsolePlayer
        }

        MethodHelper.call(executeMethod, player, args)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>?): MutableList<String> {
        if (tabCompleteMethod == null || (permission != "" && !sender.hasPermission(permission))) return mutableListOf()

        val list = mutableListOf<String>()

        val player = when (sender) {
            is Player -> find<PlayerService>()[sender.uniqueId]
            else -> ConsolePlayer
        }

        MethodHelper.call(tabCompleteMethod, player, args, list)

        return list
    }

}