package rocks.clanattack.impl.player.console.trait

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import rocks.clanattack.entry.find
import rocks.clanattack.impl.language.Language
import rocks.clanattack.impl.player.console.ConsolePlayer
import rocks.clanattack.language.LanguageService
import rocks.clanattack.player.trait.DisplayTrait
import rocks.clanattack.player.trait.get

object ConsoleDisplayTrait : DisplayTrait {

    override val name: String =
        (find<LanguageService>().defaultLanguage as Language).getPlainMessage("core.player.console.name")
            ?: "Console"

    override var color: TextColor?
        get() = ConsolePlayer.data.get<TextColor>("core.display.color")
        set(value) {
            if (value != null) ConsolePlayer.data["core.display.color"] = value
            else ConsolePlayer.data.remove("core.display.color")
        }
    override val displayName: ComponentLike
        get() = (prefix?.asComponent() ?: Component.empty())
            .append(coloredName)
            .append(suffix?.asComponent() ?: Component.empty())

    override val coloredName: ComponentLike
        get() = Component.text(name).color(color ?: TextColor.color(NamedTextColor.GRAY))

    override var prefix: ComponentLike?
        get() = ConsolePlayer.data.get<Component>("core.display.prefix")
        set(value) {
            if (value != null) ConsolePlayer.data["core.display.prefix"] = value.asComponent()
            else ConsolePlayer.data.remove("core.display.prefix")
        }
    override var suffix: ComponentLike?
        get() = ConsolePlayer.data.get<Component>("core.display.suffix")
        set(value) {
            if (value != null) ConsolePlayer.data["core.display.suffix"] = value.asComponent()
            else ConsolePlayer.data.remove("core.display.suffix")
        }

}