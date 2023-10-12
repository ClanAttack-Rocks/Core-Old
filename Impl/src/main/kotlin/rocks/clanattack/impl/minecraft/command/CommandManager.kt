package rocks.clanattack.impl.minecraft.command

import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import rocks.clanattack.entry.find
import rocks.clanattack.entry.point.EntryPoint
import rocks.clanattack.impl.minecraft.command.model.Commands
import rocks.clanattack.impl.minecraft.command.model.WhitelistedCommand
import rocks.clanattack.java.AnnotationScanner
import rocks.clanattack.minecraft.command.Command
import rocks.clanattack.minecraft.command.TabComplete
import rocks.clanattack.util.log.Logger

object CommandManager {

    private val _commands = mutableListOf<WhitelistedCommand>()

    val commands get() = _commands.toList()

    @EntryPoint
    fun enable() {
        loadWhitelistedCommands()
        registerCommands()
    }

    private fun loadWhitelistedCommands() {
        transaction {
            Commands.selectAll().forEach {
                _commands.add(
                    WhitelistedCommand(
                        it[Commands.id].value,
                        it[Commands.permission],
                        it[Commands.aliases].split(";")
                    )
                )
            }
        }
    }

    private fun registerCommands() {
        val tabCompleter = AnnotationScanner
            .getAnnotatedMethods(TabComplete::class.java)
            .associateBy { it.getDeclaredAnnotation(TabComplete::class.java).name.lowercase() }

        find<Logger>().info("Registering commands...")
        val commands = AnnotationScanner.getAnnotatedMethods(Command::class.java)
            .asSequence()
            .map { it.getDeclaredAnnotation(Command::class.java).name.lowercase() to it }
            .filter { (name, _) -> name in commands.map { cmd -> cmd.command.lowercase() } }
            .map { (name, execute)->
                val whitelistedCommand = commands.first { cmd -> cmd.command.lowercase() == name }

                RocksCommand(
                    name.lowercase(),
                    whitelistedCommand.aliases.map { it.lowercase() },
                    whitelistedCommand.permission,
                    execute,
                    tabCompleter[name]
                )
            }
            .onEach { Bukkit.getCommandMap().register("clanattack", it) }
            .count()
        find<Logger>().info("Registered $commands commands.")
    }

}