package rocks.clanattack.impl.player

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import rocks.clanattack.impl.player.trait.ConnectionTrait
import rocks.clanattack.impl.player.trait.DataTrait
import rocks.clanattack.player.trait.DiscordTrait
import rocks.clanattack.player.trait.DisplayTrait
import rocks.clanattack.player.trait.communication.CommunicationTrait
import java.util.*
import rocks.clanattack.player.Player as Interface

class Player(override val uuid: UUID) : Interface {
    override val minecraft: Player?
        get() = Bukkit.getPlayer(uuid)
    override val offline: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(uuid)
    override val communication: CommunicationTrait
        get() = TODO("Not yet implemented")

    override val connection by lazy { ConnectionTrait(this) }
    override val data by lazy { DataTrait(this) }
    override val discord: DiscordTrait
        get() = TODO("Not yet implemented")
    override val display: DisplayTrait
        get() = TODO("Not yet implemented")

    override fun hasPermission(permission: String) = minecraft?.hasPermission(permission) ?: false

}