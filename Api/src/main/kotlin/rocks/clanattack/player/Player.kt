package rocks.clanattack.player

import org.bukkit.OfflinePlayer
import rocks.clanattack.player.trait.ConnectionTrait
import rocks.clanattack.player.trait.DataTrait
import rocks.clanattack.player.trait.DiscordTrait
import org.bukkit.entity.Player as MinecraftPlayer
import rocks.clanattack.player.trait.DisplayTrait
import rocks.clanattack.player.trait.communication.CommunicationTrait
import java.util.*

/**
 * A [Player] is a wrapper around a [MinecraftPlayer] that provides additional functionality.
 *
 * The [Player] does not directly store any data, but uses the [DataTrait] instead to do so.
 * Therefore a [Player] can be created for any uuid, regardless if the player was every online yet or not.
 */
@Suppress("unused")
interface Player {

    /**
     * The uuid of the player.
     */
    val uuid: UUID

    /**
     * The name of the player.
     *
     * @see DisplayTrait.name
     */
    val name: String
        get() = display.name

    /**
     * The [MinecraftPlayer] instance of the player, or null if the player is offline.
     */
    val minecraft: MinecraftPlayer?

    /**
     * The [OfflinePlayer] instance of the player.
     */
    val offline: OfflinePlayer

    /**
     * The [CommunicationTrait] of the player.
     *
     * The [CommunicationTrait] is used to communicate with the player,
     * including amongst others sending messages, titles or playing sounds.
     */
    val communication: CommunicationTrait

    /**
     * The [ConnectionTrait] of the player.
     *
     * The [ConnectionTrait] is used to get information about the players connection,
     * including amongst others the players ip address or ping.
     */
    val connection: ConnectionTrait

    /**
     * The [DataTrait] of the player.
     *
     * The [DataTrait] is used to store data for a specific player.
     */
    val data: DataTrait

    /**
     * The [DiscordTrait] of the player.
     *
     * The [DiscordTrait] integrates with every aspect between a player and discord.
     */
    val discord: DiscordTrait

    /**
     * The [DisplayTrait] of the player.
     *
     * The [DisplayTrait] is used to get information about how the player is displayed to other players.
     */
    val display: DisplayTrait

    /**
     * Checks if the player has the given [permission].
     *
     * If the player is offline, this will always return `false`.
     */
    fun hasPermission(permission: String): Boolean

}