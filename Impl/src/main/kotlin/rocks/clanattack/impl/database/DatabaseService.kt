package rocks.clanattack.impl.database

import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.database.ChangeType
import rocks.clanattack.database.Patch
import rocks.clanattack.database.QueryResult
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.database.websocket.WebSocketSender
import rocks.clanattack.impl.database.websocket.Websocket
import rocks.clanattack.impl.util.ktor.Ktor
import rocks.clanattack.task.TaskService
import rocks.clanattack.util.extention.alsoIf
import rocks.clanattack.util.json.JsonDocument
import rocks.clanattack.util.json.get
import rocks.clanattack.util.json.json
import rocks.clanattack.util.promise.PromiseService
import java.util.*
import kotlin.reflect.KClass
import rocks.clanattack.database.DatabaseService as Interface

@Register(definition = Interface::class, depends = [TaskService::class, PromiseService::class, Ktor::class])
class DatabaseService : ServiceImplementation(), Interface {

    private val websocket = Websocket()
    private val sender = WebSocketSender(websocket)

    override fun enable() {
        val config = find<JavaPlugin>().dataFolder
            .resolve("database.json")
            .alsoIf({ !it.exists() }) {
                it.parentFile.mkdirs()
                json {
                    this["ssl"] = false
                    this["method"] = "GET"
                    this["host"] = "localhost"
                    this["port"] = 8000
                    this["path"] = "rpc"

                    this["namespace"] = "clanattack"
                    this["database"] = "production"

                    this["authentication"] = json {
                        this["type"] = "root"
                        this["username"] = "root"
                        this["password"] = "password"
                    }
                }.write(it, pretty = true)
            }
            .let { json(it) }

        websocket.start(config).get()

        val namespace = config.get<String>("namespace") ?: throw IllegalStateException("No namespace found")
        val database = config.get<String>("database") ?: throw IllegalStateException("No database found")

        val auth = config.get<JsonDocument>("authentication")
            ?: throw IllegalStateException("No authentication found")

        val type = auth.get<String>("type")
            ?: throw IllegalStateException("No authentication type found")

        if (type != "root" && type != "namespace" && type != "database")
            throw IllegalArgumentException("Invalid authentication type: $type")

        val username = auth.get<String>("username") ?: throw IllegalStateException("No username found")
        val password = auth.get<String>("password") ?: throw IllegalStateException("No password found")

        sender.send<Unit>("signin", json {
            if (type != "root") this["NS"] = namespace
            if (type == "database") this["DB"] = database

            this["user"] = username
            this["pass"] = password
        }).get()

        sender.send<Unit>("use", namespace, database).get()
    }

    override fun disable() {
        sender.send<Unit>("invalidate")
            .then { websocket.stop() }
    }

    override fun <T : Any> liveQuery(
        table: String,
        type: KClass<T>,
        callback: (ChangeType, String, T?) -> Unit
    ) = sender.send<UUID>("live", table, false)
        .then { sender.addLiveListener(it, type, callback) }

    override fun killLiveQuery(id: UUID) = sender.send<Unit>("kill", id)

    override fun <T : Any> query(
        query: String,
        args: Map<String, String>
    ) = sender.send<List<QueryResult<T>>>("query", query, args)

    override fun <T : Any> select(thing: String) = sender.send<List<T>>("select", thing)

    override fun <T : Any> create(thing: String, data: T) =
        sender.send("create", data::class, thing, data).map { it }

    override fun <T : Any> insert(thing: String, vararg data: T) = sender.send<List<T>>("insert", thing, data.toList())

    override fun <T : Any> update(thing: String, data: T) =
        sender.send("update", data::class, thing, data).map { it }

    override fun <T : Any, P : Any> merge(thing: String, merge: T) =
        sender.send<List<P>>("merge", thing, merge)

    override fun <T : Any> patch(thing: String, patches: List<Patch>) =
        sender.send<List<T>>("patch", thing, patches, false)

    override fun delete(thing: String) = sender.send<Unit>("delete", thing)
}