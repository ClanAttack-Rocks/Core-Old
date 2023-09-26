package rocks.clanattack.player

import org.bukkit.OfflinePlayer
import rocks.clanattack.player.trait.ConnectionTrait
import org.bukkit.entity.Player as MinecraftPlayer
import rocks.clanattack.player.trait.DisplayTrait
import rocks.clanattack.player.trait.PermissionTrait
import rocks.clanattack.player.trait.communication.CommunicationTrait
import java.util.*

interface Player {

    val uuid: UUID

    val name: String
        get() = display.name

    val minecraft: MinecraftPlayer?

    val offline: OfflinePlayer

    val communication: CommunicationTrait

    val connection: ConnectionTrait

    val display: DisplayTrait

    val permission: PermissionTrait

}