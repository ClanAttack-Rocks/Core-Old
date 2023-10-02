package rocks.clanattack.impl.util.ktor

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import kotlin.time.Duration.Companion.minutes

val ktor by lazy {
    HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 1.minutes.inWholeMilliseconds
        }
    }
}