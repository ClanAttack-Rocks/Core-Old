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
     * Checks if a player with the given [uuid] exists in the database.
     */
    fun exists(uuid: UUID): Boolean

    /**
     * Checks if a player with the given [name] exists in the database.
     */
    fun exists(name: String): Boolean

    /**
     * Creates a new player with the given [uuid].
     *
     * @throws IllegalArgumentException If a player with the given [uuid] or
     * the [name][FetchingService.getName] of the given uuid already exists.
     */
    @Throws(IllegalArgumentException::class)
    fun create(uuid: UUID)

    /**
     * Gets a player with the given [uuid].
     *
     * @throws IllegalArgumentException If no player with the given [uuid] exists.
     */
    @Throws(IllegalArgumentException::class)
    operator fun get(uuid: UUID): Player

    /**
     * Gets a player with the given [name].
     *
     * @throws IllegalArgumentException If no player with the given [name] exists.
     */
    @Throws(IllegalArgumentException::class)
    operator fun get(name: String): Player?

    /**
     * Gets a player by the given [offlinePlayer].
     *
     * @throws IllegalArgumentException If no player with the given [offlinePlayer]s uuid exists.
     */
    @Throws(IllegalArgumentException::class)
    operator fun get(offlinePlayer: OfflinePlayer): Player

}