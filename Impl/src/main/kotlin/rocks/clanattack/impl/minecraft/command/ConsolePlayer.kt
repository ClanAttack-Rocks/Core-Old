package rocks.clanattack.impl.minecraft.command

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import rocks.clanattack.player.Player as Interface
import rocks.clanattack.player.trait.ConnectionTrait
import rocks.clanattack.player.trait.DataTrait
import rocks.clanattack.player.trait.DiscordTrait
import rocks.clanattack.player.trait.DisplayTrait
import rocks.clanattack.player.trait.communication.CommunicationTrait
import java.util.*

object ConsolePlayer : Interface {

    override val uuid: UUID
        get() = throw IllegalStateException("The console does not have a UUID.")

    override val minecraft: Player? = null

    override val offline: OfflinePlayer
        get() = throw IllegalStateException("The console does not have an offline player.")

    override val communication: CommunicationTrait
        get() = throw IllegalStateException("The console does not have a communication trait.")

    override val connection: ConnectionTrait
        get() = throw IllegalStateException("The console does not have a connection trait.")

    override val data: DataTrait
        get() = throw IllegalStateException("The console does not have a data trait.")

    override val discord: DiscordTrait
        get() = throw IllegalStateException("The console does not have a discord trait.")

    override val display: DisplayTrait
        get() = throw IllegalStateException("The console does not have a display trait.")

    override fun hasPermission(permission: String) = true

}