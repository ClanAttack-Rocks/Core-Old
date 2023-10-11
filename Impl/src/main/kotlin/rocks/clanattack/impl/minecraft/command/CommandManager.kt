package rocks.clanattack.impl.minecraft.command

import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import rocks.clanattack.entry.point.EntryPoint
import rocks.clanattack.impl.minecraft.command.model.Commands
import rocks.clanattack.impl.minecraft.command.model.WhitelistedCommand

object CommandManager {

    private val commands = mutableListOf<WhitelistedCommand>()

    @EntryPoint
    fun enable() {
        transaction {
            Commands.selectAll().forEach {
                commands.add(
                    WhitelistedCommand(
                        it[Commands.id].value,
                        it[Commands.permission],
                        it[Commands.aliases].split(";")
                    )
                )
            }
        }

        registerCommands()
        clearCommandMap()
        clearAliases()
    }

    private fun registerCommands() {

    }

    private fun clearCommandMap() {
        val allowedCommands = commands.map { cmd -> cmd.command.lowercase() }

        Bukkit.getCommandMap()
            .knownCommands
            .filter { it.key.lowercase() !in allowedCommands }
            .toMap()
            .forEach { (key, _) -> Bukkit.getCommandMap().knownCommands.remove(key) }

    }

    private fun clearAliases() {
        Bukkit.getCommandMap()
            .knownCommands
            .forEach {  (key, value) ->
                val allowedAliases = commands.first { cmd -> cmd.command.lowercase() == key.lowercase() }.aliases
                value.aliases = value.aliases.filter { it !in allowedAliases }
            }
    }

}