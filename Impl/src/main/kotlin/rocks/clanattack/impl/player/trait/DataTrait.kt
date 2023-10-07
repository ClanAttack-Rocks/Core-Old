package rocks.clanattack.impl.player.trait

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import rocks.clanattack.player.Player
import rocks.clanattack.impl.player.model.PlayerData
import rocks.clanattack.impl.util.json.JsonDocument
import kotlin.reflect.KClass
import rocks.clanattack.player.trait.DataTrait as Interface

class DataTrait(private val player: Player) : Interface {

    override val data: Map<String, String>
        get() = transaction {
            PlayerData.select { PlayerData.player eq player.uuid }
                .associate { it[PlayerData.key] to it[PlayerData.value] }
        }

    override fun contains(key: String) = transaction {
        PlayerData.select { PlayerData.player eq player.uuid and (PlayerData.key eq key) }.count() > 0
    }

    override fun set(key: String, value: Any) {
        transaction {
            if (PlayerData.select { PlayerData.player eq player.uuid and (PlayerData.key eq key) }.count() > 0) {
                PlayerData.update({ PlayerData.player eq player.uuid and (PlayerData.key eq key) }) {
                    it[PlayerData.value] = JsonDocument.mapper.writeValueAsString(value)
                }
            } else {
                PlayerData.insert {
                    it[player] = this@DataTrait.player.uuid
                    it[PlayerData.key] = key
                    it[PlayerData.value] = JsonDocument.mapper.writeValueAsString(value)
                }
            }
        }
    }

    override fun <T : Any> get(key: String, type: KClass<T>) = transaction {
        PlayerData.select { PlayerData.player eq player.uuid and (PlayerData.key eq key) }
            .firstOrNull()
            ?.let { JsonDocument.mapper.readValue(it[PlayerData.value], type.java) }
    }

    override fun <T : Any> get(key: String, type: KClass<T>, default: T) = get(key, type) ?: default

    override fun <T : Any> modify(key: String, type: KClass<T>, block: (T?) -> T) {
        val value = block(get(key, type))
        val newValue = block(value)
        set(key, newValue)
    }

    override fun <T : Any> modify(key: String, type: KClass<T>, default: T, block: (T) -> T) {
        val value = get(key, type, default)
        val newValue = block(value)
        set(key, newValue)
    }

    override fun remove(key: String) {
        transaction {
            PlayerData.deleteWhere { PlayerData.player eq this@DataTrait.player.uuid and (PlayerData.key eq key) }
        }
    }

}