package rocks.clanattack.impl.database.websocket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import rocks.clanattack.entry.find
import rocks.clanattack.task.TaskService
import rocks.clanattack.util.json.JsonDocument
import rocks.clanattack.util.json.get

class Websocket(private val config: JsonDocument) {

    fun start() {
        val ssl = config.get<Boolean>("ssl", false)
        val method = HttpMethod.parse(config.get<String>("method", "GET"))
        val host = config.get<String>("host", "localhost")
        val port = config.get<Int>("port", 8000)
        val path = config.get<String>("path", "rpc")

        find<TaskService>().execute(detached = true) {
            find<HttpClient>().webSocket({
                this.method = method
                url(
                    if (ssl) "wss" else "ws",
                    host,
                    port,
                    path,
                )
            }) {

            }
        }
    }

}