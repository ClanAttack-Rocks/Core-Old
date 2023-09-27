package rocks.clanattack.impl.database

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.database.ChangeType
import rocks.clanattack.database.DatabaseService as Interface
import rocks.clanattack.database.Patch
import rocks.clanattack.database.QueryResult
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.task.TaskService
import rocks.clanattack.util.extention.alsoIf
import rocks.clanattack.util.extention.unit
import rocks.clanattack.util.json.JsonDocument
import rocks.clanattack.util.json.get
import rocks.clanattack.util.json.json
import rocks.clanattack.util.log.Logger
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds

@Suppress("EmptyMethod", "EmptyMethod")
@Register(definition = Interface::class)
class DatabaseService : ServiceImplementation(), Interface {

    @Volatile
    private var id = 1

    private lateinit var httpClient: HttpClient
    private lateinit var send: suspend (String, List<Any>) -> JsonDocument?

    private val answers = mutableMapOf<Int, (JsonDocument) -> Unit>()
    private val liveQueryReceivers = mutableMapOf<UUID, (JsonDocument) -> Unit>()

    override fun enable() {
        val config = find<JavaPlugin>().dataFolder
            .resolve("database.json")
            .also { it.parentFile.mkdirs() }
            .alsoIf({ !it.exists() }) {
                json {
                    this["method"] = HttpMethod.Get
                    this["scheme"] = "ws"
                    this["host"] = "127.0.0.1"
                    this["port"] = 8080
                    this["path"] = "/"

                    this["namespace"] = "clanattack"
                    this["database"] = "development"

                    this["authentication"] = json {
                        this["type"] = "root"
                        this["username"] = "development"
                        this["password"] = "development"
                    }
                }.write(it, true)
            }
            .let { json(it) }

        find<TaskService>().execute(detached = true) {
            httpClient = HttpClient(CIO) {
                install(WebSockets) {
                    pingInterval = 10_000
                }
            }

            try {
                httpClient.webSocket({
                    this.method = config.get<HttpMethod>("method", HttpMethod.Get)
                    url(
                        scheme = config.get("scheme"),
                        host = config.get("host"),
                        port = config.get("port"),
                        path = config.get("path"),
                    )
                }) {
                    launch {
                        while (true) {
                            val frame = try {
                                incoming.receive()
                            } catch (e: Exception) {
                                find<Logger>().error("Failed to receive message", e)
                                continue
                            }

                            val message = when (frame) {
                                is Frame.Text -> frame.readText()
                                is Frame.Binary -> frame.readBytes().decodeToString()
                                else -> continue
                            }.let { json(it) }

                            val id = try {
                                message.get<Int>("id")
                            } catch (e: IllegalArgumentException) {
                                find<Logger>().error("Failed to parse message", e)
                                continue
                            }

                            if (id != null) {
                                val answer = answers[id] ?: continue
                                answers.remove(id)

                                answer(message)
                            } else {
                                val result = message.get<JsonDocument>("result") ?: continue
                                if ("action" !in result || "id" !in result) continue

                                val uuid = UUID.fromString(result.get<String>("id") ?: continue)
                                if (uuid !in liveQueryReceivers) continue

                                liveQueryReceivers[uuid]?.invoke(result)
                            }
                        }
                    }

                    send = { method, params ->
                        val currentId = this@DatabaseService.id++

                        this.send(Frame.Text(json {
                            this["id"] = currentId
                            this["method"] = method
                            this["params"] = params
                        }.toString()))

                        CompletableFuture<JsonDocument?>()
                            .also { future ->
                                val task = find<TaskService>().execute(
                                    detached = true,
                                    delay = 10.seconds
                                ) {
                                    if (!future.isDone) future.complete(null)
                                }

                                answers[currentId] = {
                                    if (!future.isDone) {
                                        task.cancel()
                                        future.complete(it)
                                    }
                                }
                            }
                            .get()
                    }
                }
            } catch (e: Exception) {
                find<Logger>().error("Failed to connect to database", e)
            }
        }
    }

    override fun <T : Any> liveQuery(
        table: String,
        type: KClass<T>,
        callback: (ChangeType, String, T?) -> Unit
    ) = find<TaskService>().asCompletableFuture {
        val liveQueryId = send("liveQuery", listOf(table, false))
            ?.get<String>("id")
            ?.let { UUID.fromString(it) }
            ?: throw IllegalStateException("Failed to send liveQuery")

        liveQueryReceivers[liveQueryId] = receiver@{
            val result = it.get<JsonDocument>("result") ?: return@receiver
            val action = try {
                result.get<ChangeType>("action") ?: return@receiver
            } catch (e: IllegalArgumentException) {
                find<Logger>().error("Failed to parse action in liveQuery $liveQueryId", e)
                return@receiver
            }

            val resultDocument = result.get<JsonDocument>("result") ?: return@receiver
            val id = resultDocument.get<String>("id") ?: return@receiver
            val parsedResult = if (action != ChangeType.DELETE) result["result", type] ?: return@receiver else null

            callback(action, id, parsedResult)
        }

        return@asCompletableFuture liveQueryId
    }

    override fun liveQuery(
        table: String,
        callback: (ChangeType, String, List<Patch>) -> Unit
    ) = find<TaskService>().asCompletableFuture {
        val liveQueryId = send("liveQuery", listOf(table, true))
            ?.get<String>("id")
            ?.let { UUID.fromString(it) }
            ?: throw IllegalStateException("Failed to send liveQuery")

        liveQueryReceivers[liveQueryId] = receiver@{
            // TODO: Parse and call callback
        }

        return@asCompletableFuture liveQueryId
    }

    override fun killLiveQuery(id: UUID) = find<TaskService>().asCompletableFuture {
        unit {
            send("kill", listOf(id.toString())) ?: throw IllegalStateException("Failed to send killLiveQuery")
        }
    }

    override fun <T : Any> query(
        query: String,
        type: KClass<T>,
        args: Map<String, String>
    ): CompletableFuture<List<QueryResult<T>>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> select(thing: String, type: KClass<T>): CompletableFuture<List<T>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> create(thing: String, data: T): CompletableFuture<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> create(thing: String, data: List<T>): CompletableFuture<List<T>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> update(thing: String, data: T): CompletableFuture<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any, P : Any> merge(thing: String, merge: T, type: KClass<P>): CompletableFuture<List<P>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> patch(thing: String, patches: List<Patch>, type: KClass<T>): CompletableFuture<T> {
        TODO("Not yet implemented")
    }

    override fun delete(thing: String): CompletableFuture<Unit> {
        TODO("Not yet implemented")
    }
}