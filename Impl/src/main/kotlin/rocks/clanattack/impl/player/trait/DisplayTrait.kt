package rocks.clanattack.impl.player.trait

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import rocks.clanattack.entry.find
import rocks.clanattack.player.Player
import rocks.clanattack.minecraft.fetching.FetchingService
import rocks.clanattack.player.trait.get
import rocks.clanattack.player.trait.DisplayTrait as Interface

class DisplayTrait(private val player: Player) : Interface {

    override val name: String
        get() = find<FetchingService>().getName(player.uuid).get().value
            ?: throw IllegalStateException("Player has no name")

    override var color: TextColor?
        get() = player.data.get<TextColor>("core.display.color")
        set(value) {
            if (value != null) player.data["core.display.color"] = value
            else player.data.remove("core.display.color")
        }
    override val displayName: ComponentLike
        get() = (prefix?.asComponent() ?: Component.empty())
            .append(coloredName)
            .append(suffix?.asComponent() ?: Component.empty())

    override val coloredName: ComponentLike
        get() = Component.text(name).color(color ?: TextColor.color(NamedTextColor.GRAY))

    override var prefix: ComponentLike?
        get() = player.data.get<Component>("core.display.prefix")
        set(value) {
            if (value != null) player.data["core.display.prefix"] = value.asComponent()
            else player.data.remove("core.display.prefix")
        }
    override var suffix: ComponentLike?
        get() = player.data.get<Component>("core.display.suffix")
        set(value) {
            if (value != null) player.data["core.display.suffix"] = value.asComponent()
            else player.data.remove("core.display.suffix")
        }

}