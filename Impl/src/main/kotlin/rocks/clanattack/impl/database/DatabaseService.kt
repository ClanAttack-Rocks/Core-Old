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
import rocks.clanattack.impl.database.websocket.Websocket
import rocks.clanattack.util.extention.alsoIf
import rocks.clanattack.util.json.json
import rocks.clanattack.util.promise.Promise
import java.util.*
import kotlin.reflect.KClass

@Register(definition = Interface::class)
class DatabaseService : ServiceImplementation(), Interface {

    private lateinit var websocket: Websocket

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

        websocket = Websocket(config)
        websocket.start()
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