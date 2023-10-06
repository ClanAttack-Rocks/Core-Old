package rocks.clanattack.database

import org.jetbrains.exposed.sql.Database
import rocks.clanattack.entry.service.Service

/**
 * The [DatabaseService] is used to access the database.
 *
 * It registers in addition to itself a `Database` instance to the registry.
 */
interface DatabaseService : Service {

    /**
     * The [Database] instance.
     */
    val database: Database

    /**
     * Migrates the database to the latest version.
     *
     * This will only create missing tables and columns,
     * it will not delete any tables or columns.
     *
     * If [fresh] is true, [drop] will be called before migrating.
     */
    fun migrate(fresh: Boolean = false)

    /**
     * Drops all tables from the database.
     *
     * This will delete all data from the database.
     */
    fun drop()

}