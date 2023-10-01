package rocks.clanattack.player.trait

import io.ktor.network.sockets.*
import kotlin.time.Duration

/**
 * The [ConnectionTrait] is used to get information about the player's connection.
 */
@Suppress("unused")
interface ConnectionTrait {

    /**
     * Whether the player is online.
     */
    val online: Boolean

    /**
     * The time the player has spent on the server.
     */
    val onlineTime: Duration

    /**
     * The current ping of the player, or `null` if the player is offline.
     */
    val ping: Int?

    /**
     * The ip address of the player, or `null` if the player is offline.
     */
    val address: InetSocketAddress?

    /**
     * The ip/host and port the player used to connect to the server, or `null` if the player is offline.
     */
    val virtualHost: InetSocketAddress?

    /**
     * The protocol version of the player, or `null` if the player is offline.
     */
    val protocolVersion: Int?

}