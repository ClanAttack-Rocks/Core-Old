package rocks.clanattack.impl.player

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import rocks.clanattack.impl.player.trait.*
import rocks.clanattack.impl.player.trait.communication.CommunicationTrait
import rocks.clanattack.player.trait.DiscordTrait
import java.util.*
import rocks.clanattack.player.Player as Interface

class Player(override val uuid: UUID) : Interface {
    override val minecraft: Player?
        get() = Bukkit.getPlayer(uuid)
    override val offline: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(uuid)

    override val communication by lazy { CommunicationTrait(this) }
    override val connection by lazy { ConnectionTrait(this) }
    override val data by lazy { DataTrait(this) }
    override val discord: DiscordTrait
        get() = TODO("Not yet implemented")

    override val display by lazy { DisplayTrait(this) }

    override fun hasPermission(permission: String) = minecraft?.hasPermission(permission) ?: false

}