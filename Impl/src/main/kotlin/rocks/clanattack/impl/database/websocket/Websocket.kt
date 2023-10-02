package rocks.clanattack.impl.database.websocket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import rocks.clanattack.entry.find
import rocks.clanattack.task.TaskService
import rocks.clanattack.util.extention.unit
import rocks.clanattack.util.extention.with
import rocks.clanattack.util.json.JsonDocument
import rocks.clanattack.util.json.get
import rocks.clanattack.util.json.json
import rocks.clanattack.util.promise.Promise

class Websocket() {

    private val listeners = mutableListOf<(JsonDocument) -> Boolean>()

    private lateinit var listener: Job
    var send: suspend (JsonDocument) -> Unit = { throw IllegalStateException("Websocket not started yet") }
        private set

    fun start(config: JsonDocument, done: () -> Unit) {
        val ssl = config.get<Boolean>("ssl") ?: throw IllegalStateException("No ssl found")
        val method = HttpMethod.parse(
            config.get<String>("method") ?: throw IllegalStateException("No method found")
        )

        val host = config.get<String>("host") ?: throw IllegalStateException("No host found")
        val port = config.get<Int>("port") ?: throw IllegalStateException("No port found")
        val path = config.get<String>("path") ?: throw IllegalStateException("No path found")

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
                listener = launch {
                    try {
                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> json(frame.readText())
                                    .let { listeners.filter { listener -> listener(it) } }
                                    .forEach { listeners.remove(it) }

                                is Frame.Binary -> json(frame.readBytes().toString(Charsets.UTF_8))
                                    .let { listeners.filter { listener -> listener(it) } }
                                    .forEach { listeners.remove(it) }

                                else -> {}
                            }
                        }
                    } catch (_: ClosedReceiveChannelException) {
                    }
                }

                send = { send(it.toString()) }

                done()
            }
        }
    }

    fun stop() {
        listener.cancel()
    }

    fun addListener(listener: (JsonDocument) -> Boolean) = unit { listeners.add(listener) }

}