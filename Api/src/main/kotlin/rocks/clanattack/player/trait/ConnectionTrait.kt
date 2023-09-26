package rocks.clanattack.player.trait

import io.ktor.network.sockets.*
import kotlin.time.Duration

interface ConnectionTrait {

    val online: Boolean

    val onlineTime: Duration

    val address: InetSocketAddress

    val virtualHost: InetSocketAddress

    val protocolVersion: Int

}