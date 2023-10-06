package rocks.clanattack.impl.language

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import rocks.clanattack.entry.find
import rocks.clanattack.language.LanguageService
import rocks.clanattack.language.Replacement as Interface

class Replacement(private val language: Language, private val message: String) : Interface {

    private val tagResolvers = mutableListOf<TagResolver>()

    override fun component(placeholder: String, component: ComponentLike) {
        tagResolvers.add(Placeholder.component(placeholder, component))
    }

    override fun unparsed(placeholder: String, unparsed: String) {
        tagResolvers.add(Placeholder.unparsed(placeholder, unparsed))
    }

    override fun parsed(placeholder: String, parsed: String) {
        tagResolvers.add(Placeholder.parsed(placeholder, parsed))
    }

    override fun number(placeholder: String, number: Number) {
        tagResolvers.add(Formatter.number(placeholder, number))
    }

    override fun date(placeholder: String, date: LocalDateTime) {
        tagResolvers.add(Formatter.date(placeholder, date.toJavaLocalDateTime()))
    }

    override fun choice(placeholder: String, choice: Int) {
        tagResolvers.add(Formatter.choice(placeholder, choice))
    }

    override fun message(placeholder: String, message: String) =
        component(placeholder, language.getMessage(message))

    fun get(): ComponentLike {
        find<LanguageService>().placeholders.forEach { (key, value) -> message(key, value) }
        return MiniMessage.miniMessage().deserialize(message, *tagResolvers.toTypedArray())
    }


}