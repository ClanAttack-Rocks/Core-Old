package rocks.clanattack.player

import org.bukkit.OfflinePlayer
import rocks.clanattack.entry.service.Service
import rocks.clanattack.minecraft.fetching.FetchingService
import java.util.UUID

/**
 * The [PlayerService] is used to interact with [Player]s.
 */
@Suppress("unused")
interface PlayerService : Service {

    /**
     * Gets a player with the given [uuid].
     */
    operator fun get(uuid: UUID): Player

    /**
     * Gets a player with the given [name].
     */
    operator fun get(name: String): Player?

    /**
     * Gets a player by the given [offlinePlayer].
     */
    operator fun get(offlinePlayer: OfflinePlayer): Player

}