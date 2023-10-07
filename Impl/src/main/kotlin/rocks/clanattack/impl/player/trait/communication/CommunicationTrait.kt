package rocks.clanattack.impl.player.trait.communication

import kotlinx.datetime.LocalDateTime
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.title.Title
import org.bukkit.Instrument
import org.bukkit.Location
import org.bukkit.Note
import rocks.clanattack.entry.find
import rocks.clanattack.language.Language
import rocks.clanattack.language.LanguageService
import rocks.clanattack.language.Replacement
import rocks.clanattack.player.Player
import rocks.clanattack.player.trait.communication.CommunicationPriority
import rocks.clanattack.player.trait.get
import rocks.clanattack.util.extention.diff
import rocks.clanattack.util.extention.now
import rocks.clanattack.util.extention.plus
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import net.kyori.adventure.bossbar.BossBar as AdventureBossBar
import rocks.clanattack.player.trait.communication.CommunicationTrait as Interface

class CommunicationTrait(private val player: Player) : Interface {
    override var language: Language
        get() = player.data.get<String>("core.language")
            ?.let { find<LanguageService>().getLanguage(it) }
            ?: find<LanguageService>().defaultLanguage
        set(value) = player.data.set("core.language", value.isoCode)

    override var playerListHeader: ComponentLike?
        get() = player.minecraft?.playerListHeader()
        set(value) {
            if (value != null) player.minecraft?.sendPlayerListHeader(value)
            else throw UnsupportedOperationException("Removing the player list header is not supported")
        }

    override var playerListFooter: ComponentLike?
        get() = player.minecraft?.playerListFooter()
        set(value) {
            if (value != null) player.minecraft?.sendPlayerListFooter(value)
            else throw UnsupportedOperationException("Removing the player list footer is not supported")
        }

    private val _bossBars = mutableMapOf<String, BossBar>()
    override val bossBars
        get() = _bossBars.toMap()

    override fun sendMessage(key: String, replacement: Replacement.() -> Unit) {
        player.minecraft?.sendMessage(language.getMessage(key, replacement))
    }

    override fun sendActionbar(
        key: String,
        duration: Duration,
        priority: CommunicationPriority,
        replacement: Replacement.() -> Unit
    ) {
        if (player.uuid in ActionBar) {
            val information = ActionBar[player.uuid]!!
            if (information.priority.ordinal > priority.ordinal) return
        }

        ActionBar[player.uuid] = ActionBarInformation(
            language.getMessage(key, replacement),
            LocalDateTime.now() + duration,
            priority
        )
    }

    override fun sendTitle(
        titleKey: String,
        subtitleKey: String,
        fadeIn: Duration,
        stay: Duration,
        fadeOut: Duration,
        priority: CommunicationPriority,
        replacement: Replacement.() -> Unit
    ) {
        val lastTitlePriority =
            player.data.get<CommunicationPriority>("core.communication.title.priority", CommunicationPriority.LOWEST)
        val lastTitleEnd = player.data.get<LocalDateTime>("core.communication.title.end", LocalDateTime.now())

        if (lastTitleEnd.diff(LocalDateTime.now()) > Duration.ZERO && lastTitlePriority > priority) return

        player.data["core.communication.title.priority"] = priority
        player.data["core.communication.title.end"] = LocalDateTime.now() + fadeIn + stay + fadeOut

        player.minecraft?.showTitle(
            Title.title(
                language.getMessage(titleKey, replacement).asComponent(),
                language.getMessage(subtitleKey, replacement).asComponent(),
                Title.Times.times(
                    fadeIn.toJavaDuration(),
                    stay.toJavaDuration(),
                    fadeOut.toJavaDuration()
                )
            )
        )
    }

    override fun createBossBar(id: String, key: String, replacement: Replacement.() -> Unit): BossBar {
        val adventure = AdventureBossBar.bossBar(
            language.getMessage(key, replacement),
            0f,
            AdventureBossBar.Color.PURPLE,
            AdventureBossBar.Overlay.PROGRESS
        )
        adventure.addViewer(player.minecraft!!)

        val bossBar = BossBar(player, id, key, language, adventure)
        _bossBars[id] = bossBar

        return bossBar
    }

    override fun getBossBar(id: String) = _bossBars[id]

    override fun getOrCreateBossBar(id: String, key: String, replacement: Replacement.() -> Unit) =
        getBossBar(id) ?: createBossBar(id, key, replacement)

    fun removeBossBar(id: String) {
        _bossBars.remove(id)
    }

    override fun kick(key: String, replacement: Replacement.() -> Unit) {
        player.minecraft?.kick(language.getMessage(key, replacement).asComponent())
    }

    override fun playSound(sound: Sound, emitter: Sound.Emitter?) {
        player.minecraft?.playSound(sound, emitter ?: Sound.Emitter.self())
    }

    override fun playSound(sound: Sound, location: Location) = playSound(sound, location.x, location.y, location.z)

    override fun playSound(sound: Sound, x: Double, y: Double, z: Double) {
        player.minecraft?.playSound(sound, x, y, z)
    }

    override fun stopSound(sound: Sound) {
        player.minecraft?.stopSound(sound)
    }

    override fun stopSound(sound: SoundStop) {
        player.minecraft?.stopSound(sound)
    }

    override fun stopAllSounds() {
        player.minecraft?.stopAllSounds()
    }

    override fun playNote(instrument: Instrument, note: Note, location: Location?) {
        player.minecraft?.playNote(location ?: player.minecraft?.location!!, instrument, note)
    }

    override fun playNote(instrument: Instrument, note: Note, x: Double, y: Double, z: Double) =
        if (player.connection.online) playNote(instrument, note, Location(player.minecraft!!.world, x, y, z))
        else Unit

}