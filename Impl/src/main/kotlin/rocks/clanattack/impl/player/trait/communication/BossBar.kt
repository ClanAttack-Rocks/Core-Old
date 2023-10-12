package rocks.clanattack.impl.player.trait.communication

import net.kyori.adventure.text.ComponentLike
import rocks.clanattack.language.Language
import rocks.clanattack.language.Replacement
import rocks.clanattack.player.Player
import rocks.clanattack.util.extention.unit
import net.kyori.adventure.bossbar.BossBar as AdventureBossBar
import rocks.clanattack.player.trait.communication.BossBar as Interface

class BossBar(
    override val player: Player,
    override val id: String,
    override val key: String,
    override val language: Language,
    adventure: AdventureBossBar,
) : Interface {

    private var disposed = false
    private val adventure = adventure
        get() = if (disposed) throw IllegalStateException("BossBar has been disposed") else field

    override val name: ComponentLike
        get() = adventure.name()

    override var progress: Float
        get() = adventure.progress()
        set(value) {
            adventure.progress(value)
        }
    override var color: AdventureBossBar.Color
        get() = adventure.color()
        set(value) {
            adventure.color(value)
        }
    override var overlay: AdventureBossBar.Overlay
        get() = adventure.overlay()
        set(value) {
            adventure.overlay(value)
        }
    override val flags: List<AdventureBossBar.Flag>
        get() = adventure.flags().toList()

    override fun setName(key: String, replacement: Replacement.() -> Unit) {
        adventure.name(language.getMessage(key, replacement))
    }

    override fun hasFlag(flag: AdventureBossBar.Flag) = adventure.hasFlag(flag)

    override fun addFlag(vararg flag: AdventureBossBar.Flag) = unit { adventure.addFlags(*flag) }

    override fun removeFlag(vararg flag: AdventureBossBar.Flag) = unit { adventure.removeFlags(*flag) }

    override fun setFlags(vararg flag: AdventureBossBar.Flag) {
        clearFlags()
        addFlag(*flag)
    }

    override fun clearFlags() {
        adventure.flags().forEach { adventure.removeFlag(it) }
    }

    override fun hide() {
        if (player.connection.online) adventure.removeViewer(player.minecraft!!)
    }

    override fun show() {
        if (player.connection.online) adventure.addViewer(player.minecraft!!)
    }

    override fun dispose() {
        hide()
        (player.communication as CommunicationTrait).removeBossBar(id)
        disposed = true
    }
}