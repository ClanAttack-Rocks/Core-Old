package rocks.clanattack.impl.language

import net.kyori.adventure.text.ComponentLike
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import rocks.clanattack.impl.language.model.Languages
import rocks.clanattack.impl.language.model.Messages
import rocks.clanattack.language.Replacement
import rocks.clanattack.language.Language as Interface

class Language(override val isoCode: String) : Interface {

    private val messageCache = mutableMapOf<String, String>()

    override var name: String
        get() = Languages.select { Languages.id eq isoCode }.first()[Languages.name]
        set(value) {
            Languages.update({ Languages.id eq isoCode }) { it[name] = value }
        }

    override var skull: String
        get() = Languages.select { Languages.id eq isoCode }.first()[Languages.skull]
        set(value) {
            Languages.update({ Languages.id eq isoCode }) { it[skull] = value }
        }

    override var enabled: Boolean
        get() = Languages.select { Languages.id eq isoCode }.first()[Languages.enabled]
        set(value) {
            Languages.update({ Languages.id eq isoCode }) { it[enabled] = value }
        }

    override fun getMessage(key: String, replacement: Replacement.() -> Unit): ComponentLike {
        var message = messageCache[key]
        if (message == null) {
            message = Messages.select { Messages.language eq isoCode and (Messages.key eq key) }.firstOrNull()
                ?.get(Messages.value) ?: "<red>Message with key $key not found</red>"

            messageCache[key] = message
        }

        return Replacement(this, message)
            .apply(replacement)
            .get()
    }
}