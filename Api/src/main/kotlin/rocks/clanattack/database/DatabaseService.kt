package rocks.clanattack.database

import rocks.clanattack.entry.service.Service

/**
 * The [DatabaseService] is used to access the database.
 */
interface DatabaseService : Service {

    /**
     * Migrates the database to the latest version.
     *
     * This will only create missing tables and columns,
     * it will not delete any tables or columns.
     *
     * If [fresh] is true, [wipe] will be called before migrating.
     */
    fun migrate(fresh: Boolean = false)

    /**
     * Wipes the database.
     *
     * This will delete all data and tables.
     */
    fun wipe()

}