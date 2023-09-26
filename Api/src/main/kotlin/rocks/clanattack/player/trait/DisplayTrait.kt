package rocks.clanattack.player.trait

import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextColor

interface DisplayTrait {

    val name: String

    var color: TextColor?

    val displayName: ComponentLike

    val coloredName: ComponentLike

    var prefix: ComponentLike?

    var suffix: ComponentLike?

}