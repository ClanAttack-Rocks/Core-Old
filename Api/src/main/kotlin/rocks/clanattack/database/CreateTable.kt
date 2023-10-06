package rocks.clanattack.database

import org.jetbrains.exposed.sql.Table

/**
 * Registers a [Exposed-Table][Table] to be automatically migrated with the [DatabaseService].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CreateTable
