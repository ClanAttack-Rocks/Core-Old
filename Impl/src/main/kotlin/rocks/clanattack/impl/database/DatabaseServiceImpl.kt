package rocks.clanattack.impl.database

import rocks.clanattack.database.ChangeType
import rocks.clanattack.database.DatabaseService
import rocks.clanattack.database.Patch
import rocks.clanattack.database.QueryResult
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

@Suppress("EmptyMethod", "EmptyMethod")
@Register(definition = DatabaseService::class)
class DatabaseServiceImpl : ServiceImplementation(), DatabaseService {

    override fun <T : Any> liveQuery(
        table: String,
        type: KClass<T>,
        callback: (ChangeType, String, T?) -> Unit
    ): CompletableFuture<UUID> {
        TODO("Not yet implemented")
    }

    override fun liveQuery(
        table: String,
        callback: (ChangeType, String, List<Patch>) -> Unit
    ): CompletableFuture<UUID> {
        TODO("Not yet implemented")
    }

    override fun killLiveQuery(id: UUID): CompletableFuture<Unit> {
        TODO("Not yet implemented")
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