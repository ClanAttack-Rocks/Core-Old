package rocks.clanattack.impl.database

import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.util.extention.alsoIf
import rocks.clanattack.util.json.json
import rocks.clanattack.database.DatabaseService as Interface
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import rocks.clanattack.database.CreateTable
import rocks.clanattack.java.AnnotationScanner
import rocks.clanattack.java.ClassHelper
import rocks.clanattack.util.extention.loop
import rocks.clanattack.util.json.JsonDocument
import rocks.clanattack.util.json.get
import rocks.clanattack.util.log.Logger

@Register(definition = Interface::class)
class DatabaseService : ServiceImplementation(), Interface {

    override fun enable() {
        val config = find<JavaPlugin>().dataFolder
            .resolve("database.json")
            .alsoIf({ !it.exists() }) {
                it.parentFile.mkdirs()
                json {
                    this["jdbcUrl"] = "jdbc:mysql://[host]:[port]/[database]"
                    this["username"] = "username"
                    this["password"] = "password"

                    this["connectionTimeout"] = 30000
                    this["idleTimeout"] = 600000
                    this["maxLifetime"] = 1800000
                    this["minimumIdle"] = 3
                    this["maximumPoolSize"] = 20
                }.write(it, pretty = true)
            }
            .let { json(it) }

        val dataSource = HikariDataSource(HikariConfig().apply {
            modify(config)
            validate()
        })

        find<Logger>().info("Connected to database...")
        Database.connect(dataSource)

        @OptIn(ExperimentalKeywordApi::class)
        DatabaseConfig {
            preserveKeywordCasing = true
        }
        find<Logger>().info("Connected to database!")

        migrate()
    }

    private fun HikariConfig.modify(config: JsonDocument) {
        jdbcUrl = config.get<String>("jdbcUrl") ?: throw IllegalStateException("jdbcUrl in database.json is not set")

        username = config.get<String>("username") ?: throw IllegalStateException("username in database.json is not set")
        password = config.get<String>("password") ?: throw IllegalStateException("password in database.json is not set")

        connectionTimeout = config.get<Long>("connectionTimeout")
            ?: throw IllegalStateException("connectionTimeout in database.json is not set")
        idleTimeout = config.get<Long>("idleTimeout")
            ?: throw IllegalStateException("idleTimeout in database.json is not set")
        maxLifetime = config.get<Long>("maxLifetime")
            ?: throw IllegalStateException("maxLifetime in database.json is not set")
        minimumIdle =
            config.get<Int>("minimumIdle") ?: throw IllegalStateException("minimumIdle in database.json is not set")
        maximumPoolSize = config.get<Int>("maximumPoolSize")
            ?: throw IllegalStateException("maximumPoolSize in database.json is not set")

        poolName = config.get<String>("poolName", "HikariCP")

        addDataSourceProperty("cachePrepStmts", config.get<Boolean>("cachePrepStmts", true))
        addDataSourceProperty("prepStmtCacheSize", config.get<Int>("prepStmtCacheSize", 250))
        addDataSourceProperty("prepStmtCacheSqlLimit", config.get("prepStmtCacheSqlLimit", 2048))
        addDataSourceProperty("useServerPrepStmts", config.get("useServerPrepStmts", true))
        addDataSourceProperty("useLocalSessionState", config.get("useLocalSessionState", true))
        addDataSourceProperty("rewriteBatchedStatements", config.get("rewriteBatchedStatements", true))
        addDataSourceProperty("cacheResultSetMetadata", config.get("cacheResultSetMetadata", true))
        addDataSourceProperty("cacheServerConfiguration", config.get("cacheServerConfiguration", true))
        addDataSourceProperty("elideSetAutoCommits", config.get("elideSetAutoCommits", true))
        addDataSourceProperty("maintainTimeStats", config.get("maintainTimeStats", false))
        addDataSourceProperty("autoReconnect", config.get("autoReconnect", true))
    }

    override fun migrate(fresh: Boolean) {
        find<Logger>().info("Migrating database ${if (fresh) "(fresh)" else ""}...")

        if (fresh) clear()

        val tables = findTables()
        find<Logger>().info("Found ${tables.size} tables to migrate...")
        transaction { SchemaUtils.createMissingTablesAndColumns(*tables.toTypedArray()) }

        find<Logger>().info("Migrated database!")
    }

    override fun wipe() {
        find<Logger>().info("Wiping database...")
        clear()
        find<Logger>().info("Wiped database!")
    }

    private fun clear() {
        find<Logger>().info("Clearing tables...")
        val tables = findTables().reversed()

        val tries = loop(3) { i, stop ->
            try {
                transaction {
                    tables
                        .filter { it.exists() }
                        .forEach { it.deleteAll() }
                }

                stop()
            } catch (e: Exception) {
                if (i == 2) {
                    find<Logger>().error("Failed to clear tables (retried 3 times), giving up...")
                    throw e
                }

                find<Logger>().warn("Failed to clear tables! Retrying... (${i + 1}/3)")
            }
        }

        find<Logger>().info("Cleared tables after $tries try/tries!")

        find<Logger>().info("Dropping tables...")
        transaction { SchemaUtils.drop(*tables.toTypedArray()) }
        find<Logger>().info("Dropped tables!")
    }

    private fun findTables() = AnnotationScanner.getAnnotatedClasses(CreateTable::class.java)
        .sortedBy { it.getDeclaredAnnotation(CreateTable::class.java).order }
        .map { ClassHelper.createInstance(it) }
        .filterIsInstance<Table>()

}