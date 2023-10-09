package rocks.clanattack.impl.player.console.trait

import kotlinx.datetime.LocalDateTime
import rocks.clanattack.player.trait.ConnectionTrait
import java.net.InetSocketAddress
import kotlin.time.Duration

object ConsoleConnectionTrait : ConnectionTrait {

    override val online = false
    override val onlineTime = Duration.ZERO
    override val joined: LocalDateTime? = null
    override val onlineDuration: Duration? = null
    override val ping: Int? = null
    override val address: InetSocketAddress? = null
    override val virtualHost: InetSocketAddress? = null
    override val protocolVersion: Int? = null


}