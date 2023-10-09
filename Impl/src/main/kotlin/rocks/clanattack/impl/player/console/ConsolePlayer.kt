package rocks.clanattack.impl.player.console

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import rocks.clanattack.impl.player.console.trait.ConsoleCommunicationTrait
import rocks.clanattack.impl.player.console.trait.ConsoleConnectionTrait
import rocks.clanattack.impl.player.console.trait.ConsoleDisplayTrait
import rocks.clanattack.impl.player.trait.DataTrait
import rocks.clanattack.impl.player.trait.DiscordTrait
import rocks.clanattack.player.trait.DisplayTrait
import rocks.clanattack.player.trait.communication.CommunicationTrait
import java.util.*
import rocks.clanattack.player.Player as Interface

object ConsolePlayer : Interface {

    override val uuid: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    override val minecraft: Player? = null

    override val offline: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(uuid)

    override val communication = ConsoleCommunicationTrait
    override val connection = ConsoleConnectionTrait
    override val data by lazy { DataTrait(this) }
    override val discord by lazy { DiscordTrait(this) }
    override val display = ConsoleDisplayTrait

    override fun hasPermission(permission: String) = true

}