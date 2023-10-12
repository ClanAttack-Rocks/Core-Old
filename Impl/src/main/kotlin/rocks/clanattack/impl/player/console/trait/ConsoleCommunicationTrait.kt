package rocks.clanattack.impl.player.console.trait

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Bukkit
import org.bukkit.Instrument
import org.bukkit.Location
import org.bukkit.Note
import rocks.clanattack.entry.find
import rocks.clanattack.impl.player.console.ConsolePlayer
import rocks.clanattack.language.Language
import rocks.clanattack.language.LanguageService
import rocks.clanattack.language.Replacement
import rocks.clanattack.player.trait.communication.BossBar
import rocks.clanattack.player.trait.communication.CommunicationPriority
import rocks.clanattack.player.trait.communication.CommunicationTrait
import rocks.clanattack.util.format.DateTimeService
import kotlin.time.Duration

object ConsoleCommunicationTrait : CommunicationTrait {

    override var language: Language
        get() = find<LanguageService>().defaultLanguage
        set(_) {}

    override var playerListHeader: ComponentLike? = null
    override var playerListFooter: ComponentLike? = null

    private val _bossBars = mutableMapOf<String, BossBar>()
    override val bossBars
        get() = _bossBars.toMap()

    override fun sendMessage(key: String, replacement: Replacement.() -> Unit) =
        Bukkit.getConsoleSender().sendMessage(language.getMessage("core.player.console.message") {
            component("message", language.getMessage(key, replacement))
        })

    override fun sendMessage(message: ComponentLike) {
        Bukkit.getConsoleSender().sendMessage(message)
    }

    override fun sendActionbar(
        key: String,
        duration: Duration,
        priority: CommunicationPriority,
        replacement: Replacement.() -> Unit
    ) = sendMessage("core.player.console.actionbar") {
        component("message", language.getMessage(key, replacement))
    }

    override fun sendTitle(
        titleKey: String,
        subtitleKey: String,
        fadeIn: Duration,
        stay: Duration,
        fadeOut: Duration,
        priority: CommunicationPriority,
        replacement: Replacement.() -> Unit
    ) = sendMessage("core.player.console.title") {
        component("title", language.getMessage(titleKey, replacement))
        component("subtitle", language.getMessage(subtitleKey, replacement))
        component("fadeIn", find<DateTimeService>().formatDuration(fadeIn, language))
        component("stay", find<DateTimeService>().formatDuration(stay, language))
        component("fadeOut", find<DateTimeService>().formatDuration(fadeOut, language))
    }

    override fun createBossBar(id: String, key: String, replacement: Replacement.() -> Unit): BossBar {
        val message = language.getMessage(key, replacement)
        sendMessage("core.player.console.bossbar.create") {
            unparsed("id", id)
            component("message", message)
        }

        val bossBar = ConsoleBossBar(ConsolePlayer, id, key, language, message)
        _bossBars[id] = bossBar

        return bossBar
    }

    override fun getBossBar(id: String) = _bossBars[id]

    override fun getOrCreateBossBar(id: String, key: String, replacement: Replacement.() -> Unit) =
        getBossBar(id) ?: createBossBar(id, key, replacement)

    fun removeBossBar(id: String) {
        _bossBars.remove(id)
    }

    override fun kick(key: String, replacement: Replacement.() -> Unit) = sendMessage("core.player.console.kick") {
        component("message", language.getMessage(key, replacement))
    }

    override fun playSound(sound: Sound, emitter: Sound.Emitter?) = sendSoundMessage(sound)

    override fun playSound(sound: Sound, location: Location) = sendSoundMessage(sound)

    override fun playSound(sound: Sound, x: Double, y: Double, z: Double) = sendSoundMessage(sound)

    private fun sendSoundMessage(sound: Sound) = sendMessage("core.player.console.sound") {
        unparsed("sound", sound.name().asString())
    }

    override fun stopSound(sound: Sound) = sendMessage("core.player.console.sound.stop.one") {
        unparsed("sound", sound.name().asString())
    }

    override fun stopSound(sound: SoundStop) = sendMessage("core.player.console.sound.stop.many") {
        unparsed("sound", sound.sound()?.asString() ?: "all")
        unparsed("source", sound.source()?.name ?: "all")
    }

    override fun stopAllSounds() = sendMessage("core.player.console.sound.stop.all")

    override fun playNote(instrument: Instrument, note: Note, location: Location?) =
        sendMessage("core.player.console.sound.note") {
            unparsed("instrument", instrument.name)
            unparsed("note.tone", note.tone.name)
            number("note.octave", note.octave)
            unparsed("note.sharped", note.isSharped.toString())
        }

    override fun playNote(instrument: Instrument, note: Note, x: Double, y: Double, z: Double) =
        playNote(instrument, note, null)
}