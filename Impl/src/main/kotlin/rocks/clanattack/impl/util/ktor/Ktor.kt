package rocks.clanattack.impl.util.ktor

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import rocks.clanattack.entry.registry
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.Service
import rocks.clanattack.entry.service.ServiceImplementation
import kotlin.time.Duration.Companion.minutes

interface KtorInterface : Service

@Register(definition = KtorInterface::class)
class Ktor : ServiceImplementation(), KtorInterface {

    lateinit var ktor: HttpClient
        private set

    override fun enable() {
        ktor = HttpClient(CIO) {
            install(WebSockets) {
                pingInterval = 1.minutes.inWholeMilliseconds
            }
        }

        registry.add(ktor)
    }

    override fun disable() {
        ktor.close()
    }

}