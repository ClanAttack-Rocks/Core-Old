package rocks.clanattack.database

import rocks.clanattack.entry.service.Service
import rocks.clanattack.util.optional.Optional
import rocks.clanattack.util.optional.asOptional
import rocks.clanattack.util.promise.Promise
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * The [DatabaseService] integrates with [SurrealDB](https://surrealdb.com/) to provide access to the database.
 */
@Suppress("unused")
interface DatabaseService : Service {

    /**
     * Creates a SurrealDB live query on the given [table], witch will call the [callback] on every change on the table.
     *
     * The callback contains the type of the change, the id of the changed object and the changed object itself,
     * witch is null when the type is [ChangeType.DELETE].
     *
     * The returned [CompletableFuture] can be used to cancel the live query.
     *
     * @throws IllegalStateException If the data got from the database could not be parsed into the given [type].
     */
    @Throws(IllegalStateException::class)
    fun <T : Any> liveQuery(
        table: String,
        type: KClass<T>,
        callback: (ChangeType, String, T?) -> Unit,
    ): Promise<UUID>

    /**
     * Cancels the given live query by its [id].
     */
    fun killLiveQuery(id: UUID): Promise<Unit>

    /**
     * Executes the given [query] with the given [args] on the database and returns the result.
     *
     * The [query] can contain multiple statements (separated by `;`) for each statement a [QueryResult],
     * that tries to parse the result into the given [type], will be returned.
     *
     * @throws IllegalStateException If the data got from the database could not be parsed into the given [type].
     */
    @Throws(IllegalStateException::class)
    fun <T : Any> query(
        query: String,
        args: Map<String, String> = emptyMap(),
    ): Promise<List<QueryResult<T>>>

    /**
     * Executes the given [query] with the given [args] on the database and returns the result.
     *
     * The [query] can contain multiple statements (separated by `;`), however only the first statement will be
     * evaluated and the result will be returned. With this the api will try to parse the result into the given [type].
     *
     * @throws IllegalStateException If the data got from the database could not be parsed into the given [type].
     */
    @Throws(IllegalStateException::class)
    fun <T : Any> singleQuery(
        query: String,
        args: Map<String, String> = emptyMap(),
    ): Promise<Optional<QueryResult<T>>> = this.query<T>(query, args).map { it.firstOrNull().asOptional() }

    /**
     * Selects all records that match the given [thing] and tries to parse them into the given [type].
     *
     * @throws IllegalStateException If the data got from the database could not be parsed into the given [type].
     */
    @Throws(IllegalStateException::class)
    fun <T : Any> select(thing: String): Promise<List<T>>

    /**
     * Selects a single record that matches the given [thing] and tries to parse it into the given [type].
     *
     * @throws IllegalStateException If the data got from the database could not be parsed into the given [type].
     */
    @Throws(IllegalStateException::class)
    fun <T : Any> singleSelect(thing: String): Promise<Optional<T>> =
        this.select<T>(thing).map { it.firstOrNull().asOptional() }

    /**
     * Creates the given [data] in the given [thing].
     */
    fun <T : Any> create(thing: String, data: T): Promise<T>

    /**
     * Inserts the given [data] in the given [thing].
     */
    fun <T : Any> insert(thing: String, vararg data: T): Promise<List<T>>

    /**
     * Creates the given [data] in the given [thing].
     */
    fun <T : Any> insertSingle(thing: String, data: T): Promise<T> = this.insert(thing, data).map { it.first() }

    /**
     * Updates the given [data] in the given [thing].
     *
     * This will override the whole object in the database.
     *
     * [thing] must be a record id and can not be a table.
     */
    fun <T : Any> update(thing: String, data: T): Promise<T>

    /**
     * Merge the given [data] in the given [thing].
     *
     * This will preserve the existing data in the database and only override the given fields,
     * all changed records will be parsed into the given [type][T] and returned.
     */
    fun <T : Any, P : Any> merge(
        thing: String,
        merge: T
    ): Promise<List<P>>

    /**
     * Merge the given [data] in the given [thing].
     *
     * This will preserve the existing data in the database and only override the given fields,
     * all changed records will be parsed into the given [type][T] and returned.
     */
    fun <T : Any, P : Any> mergeSingle(
        thing: String,
        merge: T
    ): Promise<P> = this.merge<T, P>(thing, merge).map { it.first() }


    /**
     * Patches the given [thing] with the given [patches].
     *
     * This will return the patched object.
     */
    fun <T : Any> patch(
        thing: String,
        patches: List<Patch>
    ): Promise<List<T>>

    /**
     * Patches the given [thing] with the given [patches].
     *
     * This will return the patched object.
     */
    fun <T : Any> patchSingle(
        thing: String,
        patches: List<Patch>
    ): Promise<T> = this.patch<T>(thing, patches).map { it.first() }

    /**
     * Deletes the given [thing].
     */
    fun delete(thing: String): Promise<Unit>

}