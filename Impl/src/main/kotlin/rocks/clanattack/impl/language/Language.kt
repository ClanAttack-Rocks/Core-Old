package rocks.clanattack.impl.language

import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage
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

        if (message == null && key == "core.message.unknown") {
            return MiniMessage.miniMessage()
                .deserialize("<red>The message <u>core.message.unknown</u> in language <u>$name</u> couldn't be found")
        }

        if (message == null) {
            return getMessage("core.message.unknown") {
                unparsed("key", key)
                unparsed("language", name)
            }
        }
        
        return Replacement(this, message)
            .apply(replacement)
            .get()
    }


    fun getMessageWithoutReplacement(key: String): ComponentLike {
        val message = getPlainMessage(key)
            ?: return MiniMessage.miniMessage()
                .deserialize("<red>The message <u>$key</u> in language <u>$name</u> couldn't be found")

        return MiniMessage.miniMessage().deserialize(message)
    }

    fun getPlainMessage(key: String) = messageCache[key] ?: transaction {
        Messages.select { Messages.language eq isoCode and (Messages.key eq key) }
            .firstOrNull()
            ?.get(Messages.value)
            ?.also { messageCache[key] = it }
    }

}