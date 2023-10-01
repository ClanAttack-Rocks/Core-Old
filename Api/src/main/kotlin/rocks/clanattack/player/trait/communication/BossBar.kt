package rocks.clanattack.player.trait.communication

import net.kyori.adventure.bossbar.BossBar.Color as AdventureColor
import net.kyori.adventure.bossbar.BossBar.Flag as AdventureFlag
import net.kyori.adventure.bossbar.BossBar.Overlay as AdventureOverlay
import net.kyori.adventure.text.ComponentLike
import rocks.clanattack.language.Language
import rocks.clanattack.language.Replacement
import rocks.clanattack.player.Player

/**
 * A [BossBar] is a bar that is displayed at the top of the screen.
 */
@Suppress("unused")
interface BossBar {

    /**
     * The [Player] the [BossBar] is displayed to.
     */
    val player: Player

    /**
     * The key of the [BossBar].
     */
    val key: String

    /**
     * The [Language] the [BossBar] is using.
     */
    var language: Language

    /**
     * The name of the [BossBar].
     */
    val name: ComponentLike

    /**
     * The progress of the [BossBar] (between 0 and 1).
     */
    var progress: Float

    /**
     * The color of the [BossBar].
     */
    var color: AdventureColor

    /**
     * The overlay used to display the [BossBar].
     */
    var overlay: AdventureOverlay

    /**
     * The flags of the [BossBar].
     */
    val flags: List<AdventureFlag>

    /**
     * Sets the name of the [BossBar].
     */
    fun setName(key: String, replacement: Replacement.() -> Unit = {})

    /**
     * Checks if the [BossBar] has the given [flag].
     */
    fun hasFlag(flag: AdventureFlag): Boolean

    /**
     * Adds the given [flag] to the [BossBar].
     */
    fun addFlag(vararg flag: AdventureFlag)

    /**
     * Removes the given [flag] from the [BossBar].
     */
    fun removeFlag(vararg flag: AdventureFlag)

    /**
     * Sets the flags of the [BossBar].
     */
    fun setFlags(vararg flag: AdventureFlag)

    /**
     * Clears all flags of the [BossBar].
     */
    fun clearFlags()

    /**
     * Temporarily hides the [BossBar].
     *
     * If the [BossBar] is already hidden, this method does nothing.
     */
    fun hide()

    /**
     * Shows the [BossBar].
     *
     * If the [BossBar] is already shown, this method does nothing.
     */
    fun show()

    /**
     * Disposes the [BossBar].
     *
     * After this method is called, changing anything on the [BossBar] will be ignored,
     * in addition the [BossBar] can no longer be shown.
     *
     * A new [BossBar] with the same [key] can be created on the [Player] afterward.
     */
    fun dispose()

}