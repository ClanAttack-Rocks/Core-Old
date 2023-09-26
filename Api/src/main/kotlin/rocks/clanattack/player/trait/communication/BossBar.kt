package rocks.clanattack.player.trait.communication

import net.kyori.adventure.bossbar.BossBar.Color as AdventureColor
import net.kyori.adventure.bossbar.BossBar.Flag as AdventureFlag
import net.kyori.adventure.bossbar.BossBar.Overlay as AdventureOverlay
import net.kyori.adventure.text.ComponentLike
import rocks.clanattack.language.Language
import rocks.clanattack.language.Replacement

interface BossBar {

    val key: String

    val language: Language

    val name: ComponentLike

    var progress: Float

    var color: AdventureColor

    var overlay: AdventureOverlay

    val flags: List<AdventureFlag>

    fun setName(key: String, replacement: Replacement.() -> Unit = {})

    fun hasFlag(flag: AdventureFlag): Boolean

    fun addFlag(vararg flag: AdventureFlag)

    fun removeFlag(vararg flag: AdventureFlag)

    fun setFlags(vararg flag: AdventureFlag)

    fun clearFlags()

}