package rocks.clanattack.database

import org.jetbrains.exposed.sql.Table

/**
 * Registers a [Exposed-Table][Table] to be automatically migrated with the [DatabaseService].
 *
 * The [order] defines the order in which the tables will be migrated,
 * tables with lower order will be migrated first,
 * tables with higher order will be migrated last.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CreateTable(val order: Int)
