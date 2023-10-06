package rocks.clanattack.impl.language

import net.kyori.adventure.text.ComponentLike
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import rocks.clanattack.impl.language.model.Languages
import rocks.clanattack.impl.language.model.Messages
import rocks.clanattack.language.Replacement
import rocks.clanattack.language.Language as Interface

class Language(override val isoCode: String) : Interface {

    private val messageCache = mutableMapOf<String, String>()

    override var name: String
        get() = transaction { Languages.select { Languages.id eq isoCode }.first()[Languages.name] }
        set(value) {
            transaction { Languages.update({ Languages.id eq isoCode }) { it[name] = value } }
        }

    override var skull: String
        get() = transaction { Languages.select { Languages.id eq isoCode }.first()[Languages.skull] }
        set(value) {
            transaction { Languages.update({ Languages.id eq isoCode }) { it[skull] = value } }
        }

    override var enabled: Boolean
        get() = transaction { Languages.select { Languages.id eq isoCode }.first()[Languages.enabled] }
        set(value) {
            transaction { Languages.update({ Languages.id eq isoCode }) { it[enabled] = value } }
        }

    override fun getMessage(key: String, replacement: Replacement.() -> Unit): ComponentLike {
        val message = getPlainMessage(key)
        return Replacement(this, message)
            .apply(replacement)
            .get()
    }

    fun getPlainMessage(key: String) = messageCache[key] ?: transaction {
        Messages.select { Messages.language eq isoCode and (Messages.key eq key) }
            .firstOrNull()
            ?.get(Messages.value)
            ?.also { messageCache[key] = it }
            ?: "<red>Message with key $key not found</red>"
    }

}