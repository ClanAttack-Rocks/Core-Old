package rocks.clanattack.impl.database

import io.ktor.http.*
import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.database.ChangeType
import rocks.clanattack.database.Patch
import rocks.clanattack.database.QueryResult
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.database.DatabaseService as Interface
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.database.websocket.WebSocketSender
import rocks.clanattack.impl.database.websocket.Websocket
import rocks.clanattack.util.extention.alsoIf
import rocks.clanattack.util.json.JsonDocument
import rocks.clanattack.util.json.get
import rocks.clanattack.util.json.json
import rocks.clanattack.util.log.Logger
import rocks.clanattack.util.promise.Promise
import java.util.*
import kotlin.reflect.KClass

@Register(definition = Interface::class)
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

        websocket.start(config) {
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
            }).mapSuspend { sender.send<Unit>("use", namespace, database).await() }
                .then { find<Logger>().info("Connected to database") }
                .catch {
                    find<Logger>().error("Could not connect to database", it)
                    find<JavaPlugin>().server.shutdown()
                }
        }
    }

    override fun disable() {
        sender.send<Unit>("invalidate")
            .then { websocket.stop() }
    }

    override fun <T : Any> liveQuery(
        table: String,
        type: KClass<T>,
        callback: (ChangeType, String, T?) -> Unit
    ): Promise<UUID> {
        TODO("Not yet implemented")
    }

    override fun liveQuery(table: String, callback: (ChangeType, String, List<Patch>) -> Unit): Promise<UUID> {
        TODO("Not yet implemented")
    }

    override fun killLiveQuery(id: UUID): Promise<Unit> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> query(
        query: String,
        type: KClass<T>,
        args: Map<String, String>
    ): Promise<List<QueryResult<T>>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> select(thing: String, type: KClass<T>): Promise<List<T>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> create(thing: String, data: T): Promise<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> create(thing: String, data: List<T>): Promise<List<T>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> update(thing: String, data: T): Promise<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any, P : Any> merge(thing: String, merge: T, type: KClass<P>): Promise<List<P>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> patch(thing: String, patches: List<Patch>, type: KClass<T>): Promise<T> {
        TODO("Not yet implemented")
    }

    override fun delete(thing: String): Promise<Unit> {
        TODO("Not yet implemented")
    }
}