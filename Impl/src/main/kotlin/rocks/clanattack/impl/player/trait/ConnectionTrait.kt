package rocks.clanattack.impl.player.trait

import kotlinx.datetime.LocalDateTime
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import rocks.clanattack.entry.find
import rocks.clanattack.player.Player
import rocks.clanattack.impl.player.PlayerService
import rocks.clanattack.minecraft.listener.Listen
import rocks.clanattack.player.trait.get
import rocks.clanattack.player.trait.modify
import rocks.clanattack.util.extention.diff
import rocks.clanattack.util.extention.now
import java.net.InetSocketAddress
import kotlin.time.Duration
import rocks.clanattack.player.trait.ConnectionTrait as Interface

class ConnectionTrait(private val player: Player) : Interface {

    override val online: Boolean
        get() = player.minecraft != null
    override val onlineTime: Duration
        get() = player.data.get<Duration>("core.connection.online", Duration.ZERO) +
                (onlineDuration ?: Duration.ZERO)

    override val joined: LocalDateTime?
        get() = if (!online) null else player.data.get<LocalDateTime>("core.connection.joined")!!

    override val onlineDuration: Duration?
        get() = joined?.diff(LocalDateTime.now())
    override val ping: Int?
        get() = player.minecraft?.ping
    override val address: InetSocketAddress?
        get() = player.minecraft?.address
    override val virtualHost: InetSocketAddress?
        get() = player.minecraft?.virtualHost
    override val protocolVersion: Int?
        get() = player.minecraft?.protocolVersion

    companion object {

        @Listen(PlayerJoinEvent::class)
        fun playerJoin(event: PlayerJoinEvent) {
            val player = find<PlayerService>()[event.player]
            player.data["core.connection.joined"] = LocalDateTime.now()
        }

        @Listen(PlayerQuitEvent::class)
        fun playerQuit(event: PlayerQuitEvent) {
            val player = find<PlayerService>()[event.player]
            player.data.modify<Duration>("core.connection.online", Duration.ZERO) {
                it + (player.connection.onlineDuration ?: Duration.ZERO)
            }
            player.data.remove("core.connection.joined")
        }

    }

}