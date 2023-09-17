package rocks.clanattack.database

import rocks.clanattack.entry.service.Service
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * The [DatabaseService] integrates with [SurrealDB](https://surrealdb.com/) to provide access to the database.
 */
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
    ): CompletableFuture<UUID>

    /**
     * Creates a SurrealDB live quire on the given [table], witch will call the [callback] on every change on the table.
     *
     * The callback contains the type of the change, the id of the changed object and patches that where applied to the
     * changed object.
     *
     * The returned [CompletableFuture] can be used to cancel the live query.
     */
    fun liveQuery(
        table: String,
        callback: (ChangeType, String, List<Patch>) -> Unit
    ): CompletableFuture<UUID>

    /**
     * Cancels the given live query by its [id].
     */
    fun killLiveQuery(id: UUID): CompletableFuture<Unit>

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
        type: KClass<T>,
        args: Map<String, String> = emptyMap(),
    ): CompletableFuture<List<QueryResult<T>>>

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
        type: KClass<T>,
        args: Map<String, String> = emptyMap(),
    ) = this.query(query, type, args).thenApply { it.firstOrNull() }

    /**
     * Selects all records that match the given [thing] and tries to parse them into the given [type].
     *
     * @throws IllegalStateException If the data got from the database could not be parsed into the given [type].
     */
    @Throws(IllegalStateException::class)
    fun <T : Any> select(thing: String, type: KClass<T>): CompletableFuture<List<T>>

    /**
     * Selects a single record that matches the given [thing] and tries to parse it into the given [type].
     *
     * @throws IllegalStateException If the data got from the database could not be parsed into the given [type].
     */
    @Throws(IllegalStateException::class)
    fun <T : Any> singleSelect(thing: String, type: KClass<T>) = this.select(thing, type).thenApply { it.firstOrNull() }

    /**
     * Creates the given [data] in the given [thing].
     */
    fun <T : Any> create(thing: String, data: T): CompletableFuture<T>

    /**
     * Inserts the given [datas] in the given [thing].
     */
    fun <T : Any> create(thing: String, datas: List<T>): CompletableFuture<List<T>>

    /**
     * Updates the given [data] in the given [thing].
     *
     * This will override the whole object in the database.
     */
    fun <T : Any> update(thing: String, data: T): CompletableFuture<T>

    /**
     * Merge the given [data] in the given [thing].
     *
     * This will preserve the existing data in the database and only override the given fields,
     * all changed records will be parsed into the given [type] and returned.
     */
    fun <T : Any, P : Any> merge(
        thing: String,
        merge: T,
        type: KClass<P>,
    ): CompletableFuture<List<P>>

    /**
     * Patches the given [thing] with the given [patches].
     *
     * This will return the patched object.
     */
    fun <T : Any> patch(
        thing: String,
        patches: List<Patch>,
        type: KClass<T>,
    ): CompletableFuture<T>

    /**
     * Deletes the given [thing].
     */
    fun delete(thing: String): CompletableFuture<Unit>

}

/**
 * Executes the given [query] with the given [args] on the database and returns the result.
 *
 * The [query] can contain multiple statements (separated by `;`) for each statement a [QueryResult],
 * that tries to parse the result into the given [type][T], will be returned.
 */
inline fun <reified T : Any> DatabaseService.query(
    query: String,
    args: Map<String, String> = emptyMap(),
) = this.query(query, T::class, args)

/**
 * Executes the given [query] with the given [args] on the database and returns the result.
 *
 * The [query] can contain multiple statements (separated by `;`), however only the first statement will be
 * evaluated and the result will be returned. With this the api will try to parse the result into the given [type][T].
 */
inline fun <reified T : Any> DatabaseService.singleQuery(
    query: String,
    args: Map<String, String> = emptyMap(),
) = this.singleQuery(query, T::class, args)

/**
 * Merges the given [data] in the given [thing].
 *
 * This will preserve the existing data in the database and only override the given fields,
 * all changed records will be parsed into the given [type][P] and returned.
 */
inline fun <reified T : Any, reified P : Any> DatabaseService.merge(thing: String, merge: T) =
    this.merge(thing, merge, P::class)