package rocks.clanattack.player.trait

import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.NamedTextColor

/**
 * The [DisplayTrait] is used to get information on how the player is displayed.
 */
interface DisplayTrait {

    /**
     * The name of the player.
     */
    val name: String

    /**
     * The color to display the name of the player in.
     *
     * If null, [NamedTextColor.GRAY] will be used.
     */
    var color: TextColor?

    /**
     * The display name of the player.
     *
     * This is always [prefix] + [coloredName] + [suffix].
     */
    val displayName: ComponentLike

    /**
     * The colored name of the player.
     *
     * This is always the [name] in the [color] specified.
     */
    val coloredName: ComponentLike

    /**
     * The prefix of the player.
     */
    var prefix: ComponentLike?

    /**
     * The suffix of the player.
     */
    var suffix: ComponentLike?

}