package rocks.clanattack.impl.player.console.trait

import net.kyori.adventure.text.ComponentLike
import rocks.clanattack.language.Language
import rocks.clanattack.language.Replacement
import rocks.clanattack.player.Player
import rocks.clanattack.player.trait.communication.BossBar
import net.kyori.adventure.bossbar.BossBar as AdventureBossBar

class ConsoleBossBar(
    override val player: Player,
    override val id: String,
    key: String,
    override val language: Language,
    name: ComponentLike,
) : BossBar {

    private var disposed = false
    override var key: String = key
        private set

    override var name: ComponentLike = name
        private set

    override var progress = 0f
        set(value) {
            if (disposed) throw IllegalStateException("BossBar has been disposed")

            field = value.coerceIn(0f, 1f)
            player.communication.sendMessage("core.player.console.bossbar.progress") {
                unparsed("key", key)
                number("progress", value)
            }
        }

    override var color = AdventureBossBar.Color.PURPLE
        set(value) {
            if (disposed) throw IllegalStateException("BossBar has been disposed")

            field = value
            player.communication.sendMessage("core.player.console.bossbar.color") {
                unparsed("key", key)
                unparsed("color", value.name)
            }
        }

    override var overlay = AdventureBossBar.Overlay.PROGRESS
        set(value) {
            if (disposed) throw IllegalStateException("BossBar has been disposed")

            field = value
            player.communication.sendMessage("core.player.console.bossbar.overlay") {
                unparsed("key", key)
                unparsed("overlay", value.name)
            }
        }

    private val _flags = mutableListOf<AdventureBossBar.Flag>()
    override val flags: List<AdventureBossBar.Flag>
        get() = _flags.toList()

    override fun setName(key: String, replacement: Replacement.() -> Unit) {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        this.key = key
        this.name = language.getMessage(key, replacement)

        player.communication.sendMessage("core.player.console.bossbar.name") {
            unparsed("key", key)
            component("name", name)
        }
    }

    override fun hasFlag(flag: AdventureBossBar.Flag): Boolean {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        return _flags.contains(flag)
    }

    override fun addFlag(vararg flag: AdventureBossBar.Flag) {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        _flags.addAll(flag)
        player.communication.sendMessage("core.player.console.bossbar.flag.add") {
            unparsed("key", key)
            unparsed("flags", flag.joinToString(", ") { it.name })
            unparsed("allFlags", _flags.joinToString(", ") { it.name })
        }
    }

    override fun removeFlag(vararg flag: AdventureBossBar.Flag) {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        _flags.removeAll(flag.toSet())
        player.communication.sendMessage("core.player.console.bossbar.flag.remove") {
            unparsed("key", key)
            unparsed("flags", flag.joinToString(", ") { it.name })
            unparsed("allFlags", _flags.joinToString(", ") { it.name })
        }
    }

    override fun setFlags(vararg flag: AdventureBossBar.Flag) {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        _flags.clear()
        _flags.addAll(flag)
        player.communication.sendMessage("core.player.console.bossbar.flag.set") {
            unparsed("key", key)
            unparsed("flags", flag.joinToString(", ") { it.name })
            unparsed("allFlags", _flags.joinToString(", ") { it.name })
        }
    }

    override fun clearFlags() {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        _flags.clear()
        player.communication.sendMessage("core.player.console.bossbar.flag.clear") {
            unparsed("key", key)
        }
    }

    override fun hide() {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        player.communication.sendMessage("core.player.console.bossbar.hide") {
            unparsed("key", key)
        }
    }

    override fun show() {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        player.communication.sendMessage("core.player.console.bossbar.show") {
            unparsed("key", key)
        }
    }

    override fun dispose() {
        if (disposed) throw IllegalStateException("BossBar has been disposed")

        disposed = true

        (player.communication as ConsoleCommunicationTrait).removeBossBar(id)
        hide()
    }
}