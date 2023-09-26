package rocks.clanattack.player.trait.communication

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import org.bukkit.Instrument
import org.bukkit.Location
import org.bukkit.Note
import rocks.clanattack.language.Language
import rocks.clanattack.language.Replacement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface CommunicationTrait {

    var language: Language

    val bossBars: Map<String, BossBar>

    fun sendMessage(key: String, replacement: Replacement.() -> Unit = {})

    fun sendActionbar(
        key: String,
        duration: Duration = 5.seconds,
        priority: CommunicationPriority = CommunicationPriority.NORMAL,
        replacement: Replacement.() -> Unit = {},
    )

    fun sendTitle(
        titleKey: String,
        subtitleKey: String,
        fadeIn: Duration = 1.seconds,
        stay: Duration = 3.seconds,
        fadeOut: Duration = 1.seconds,
        priority: CommunicationPriority = CommunicationPriority.NORMAL,
        replacement: Replacement.() -> Unit = {},
    )

    fun createBossBar(
        id: String,
        key: String,
        replacement: Replacement.() -> Unit = {},
    ): BossBar

    fun getBossBar(id: String): BossBar?

    fun getOrCreateBossBar(
        id: String,
        key: String,
        replacement: Replacement.() -> Unit = {},
    ): BossBar

    fun kick(key: String, replacement: Replacement.() -> Unit = {})

    fun playSound(sound: Sound, volume: Float = 1f, pitch: Float = 1f, emitter: Sound.Emitter? = null)

    fun playSound(sound: Sound, location: Location, volume: Float = 1f, pitch: Float = 1f)

    fun playSound(sound: Sound, x: Double, y: Double, z: Double, volume: Float = 1f, pitch: Float = 1f)

    fun stopSound(sound: Sound)

    fun stopSound(sound: SoundStop)

    fun playNote(instrument: Instrument, note: Note, location: Location? = null)

    fun playNote(instrument: Instrument, note: Note, x: Double, y: Double, z: Double)

}