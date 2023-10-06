package rocks.clanattack.impl.setting

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import rocks.clanattack.database.DatabaseService
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.setting.model.Settings
import rocks.clanattack.impl.util.json.JsonDocument
import kotlin.reflect.KClass
import rocks.clanattack.setting.SettingService as Interface

@Register(definition = Interface::class, depends = [DatabaseService::class])
class SettingService : ServiceImplementation(), Interface {

    override val settings: Map<String, String>
        get() = transaction { Settings.selectAll().associate { it[Settings.key] to it[Settings.value] } }

    override fun registerSetting(key: String, value: Any) {
        transaction {
            if (Settings.select { Settings.key eq key }.count() != 0L) return@transaction

            Settings.insert {
                it[Settings.key] = key
                it[Settings.value] = JsonDocument.mapper.writeValueAsString(value)
            }
        }
    }

    override fun set(key: String, value: Any) {
        transaction {
            Settings.update({ Settings.key eq key }) {
                it[Settings.value] = JsonDocument.mapper.writeValueAsString(value)
            }
        }
    }

    override fun contains(key: String) = transaction { Settings.select { Settings.key eq key }.count() != 0L }

    override fun <T : Any> get(key: String, type: KClass<T>) = transaction {
        val value = Settings.select { Settings.key eq key }
            .limit(1)
            .firstOrNull()
            ?.get(Settings.value)
            ?: return@transaction null

        JsonDocument.mapper.readValue(value, type.java)
    }

    override fun <T : Any> get(key: String, type: KClass<T>, default: T) = get(key, type) ?: default
}