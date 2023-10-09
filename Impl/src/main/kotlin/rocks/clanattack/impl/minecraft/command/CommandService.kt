package rocks.clanattack.impl.minecraft.command

import cloud.commandframework.CommandManager
import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.meta.SimpleCommandMeta
import cloud.commandframework.paper.PaperCommandManager
import org.bukkit.command.SimpleCommandMap
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import rocks.clanattack.database.DatabaseService
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.Service
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.minecraft.command.model.Commands
import rocks.clanattack.java.AnnotationScanner
import rocks.clanattack.java.ClassHelper
import rocks.clanattack.minecraft.command.RegisterCommand
import rocks.clanattack.player.Player
import rocks.clanattack.player.PlayerService
import rocks.clanattack.task.detached
import rocks.clanattack.task.sync

interface CommandService : Service

@Register(definition = CommandService::class, depends = [DatabaseService::class])
class CommandServiceImplementation : ServiceImplementation(), CommandService {

    private val whitelistedCommands = mutableListOf<WhitelistedCommand>()
    private lateinit var manager: CommandManager<Player>
    private lateinit var annotationParser: AnnotationParser<Player>

    override fun enable() {
        manager = PaperCommandManager(
            find(),
            CommandExecutionCoordinator.simpleCoordinator(),
            {
                if (it !is Player) return@PaperCommandManager ConsolePlayer
                find<PlayerService>()[it.uuid]
            },
            {
                if (!it.connection.online) throw IllegalStateException("The player is not online.")
                it.minecraft!!
            }
        )

        annotationParser = AnnotationParser(manager, Player::class.java) { SimpleCommandMeta.empty() }

        transaction {
            whitelistedCommands.addAll(Commands.selectAll()
                .map {
                    WhitelistedCommand(
                        it[Commands.id].value,
                        it[Commands.permission],
                        it[Commands.aliases].split(",")
                    )
                })
        }

        detached {
            AnnotationScanner.getAnnotatedClasses(RegisterCommand::class.java)
                .map { ClassHelper.createInstance(it) }
                .forEach { sync { annotationParser.parse(it) } }
        }
    }

}