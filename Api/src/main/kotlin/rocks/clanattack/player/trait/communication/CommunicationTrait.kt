package rocks.clanattack.player.trait.communication

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Instrument
import org.bukkit.Location
import org.bukkit.Note
import org.bukkit.SoundCategory
import rocks.clanattack.language.Language
import rocks.clanattack.language.Replacement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * The [CommunicationTrait] is used to communicate with the player.
 */
interface CommunicationTrait {

    /**
     * The [Language] the player is using.
     */
    var language: Language

    /**
     * The header of the player list, `null` if not set.
     */
    var playerListHeader: ComponentLike?

    /**
     * The footer of the player list, `null` if not set.
     */
    var playerListFooter: ComponentLike?

    /**
     * All [BossBar]s of the player.
     *
     * This includes the key of the [BossBar] as well as the [BossBar] itself.
     */
    val bossBars: Map<String, BossBar>

    /**
     * Sends a message to the player.
     *
     * The message will be translated using the players [language].
     *
     * @see Replacement
     */
    fun sendMessage(key: String, replacement: Replacement.() -> Unit = {})

    /**
     * Sends an actionbar to the player.
     *
     * The actionbar will stay for at least the given [duration],
     * or until another actionbar is sent with a higher [priority].
     *
     * If two actionbars are sent with the same [priority], the last one will be shown.
     *
     * The actionbar will be translated using the players [language].
     *
     * @see Replacement
     */
    fun sendActionbar(
        key: String,
        duration: Duration = 5.seconds,
        priority: CommunicationPriority = CommunicationPriority.NORMAL,
        replacement: Replacement.() -> Unit = {},
    )

    /**
     * Sends a title to the player.
     *
     * The title will stay for at least the given [stay],
     * or until another title is sent with a higher [priority].
     *
     * If two titles are sent with the same [priority], the last one will be shown.
     *
     * The title will be translated using the players [language].
     *
     * @see Replacement
     */
    fun sendTitle(
        titleKey: String,
        subtitleKey: String,
        fadeIn: Duration = 1.seconds,
        stay: Duration = 3.seconds,
        fadeOut: Duration = 1.seconds,
        priority: CommunicationPriority = CommunicationPriority.NORMAL,
        replacement: Replacement.() -> Unit = {},
    )

    /**
     * Create a new [BossBar] for the player.
     *
     * The [BossBar] will be translated using the players [language].
     *
     * @throws IllegalArgumentException If a [BossBar] with the given [id] already exists.
     * @see Replacement
     */
    @Throws(IllegalArgumentException::class)
    fun createBossBar(
        id: String,
        key: String,
        replacement: Replacement.() -> Unit = {},
    ): BossBar

    /**
     * Gets a [BossBar] of the player by its [id], or `null` if no [BossBar] with the given [id] exists.
     */
    fun getBossBar(id: String): BossBar?

    /**
     * Gets a [BossBar] of the player by its [id],
     * or creates a new one if no [BossBar] with the given [id] exists.
     *
     * The [BossBar] will be translated using the players [language].
     *
     * @see Replacement
     */
    fun getOrCreateBossBar(
        id: String,
        key: String,
        replacement: Replacement.() -> Unit = {},
    ): BossBar

    /**
     * Kick the player from the server.
     *
     * The kick message will be translated using the players [language].
     *
     * @see Replacement
     */
    fun kick(key: String, replacement: Replacement.() -> Unit = {})

    /**
     * Play a sound to the player.
     *
     * If [emitter] is null [Sound.Emitter.self] will be used.
     *
     * @see Sound.Emitter.self
     */
    fun playSound(sound: Sound, volume: Float = 1f, pitch: Float = 1f, emitter: Sound.Emitter? = null)

    /**
     * Play a sound to the player at the given [location].
     */
    fun playSound(sound: Sound, location: Location, volume: Float = 1f, pitch: Float = 1f)

    /**
     * Play a sound to the player at the given [x], [y] and [z] coordinates.
     */
    fun playSound(sound: Sound, x: Double, y: Double, z: Double, volume: Float = 1f, pitch: Float = 1f)

    /**
     * Stop a sound for the player.
     */
    fun stopSound(sound: Sound)

    /**
     * Stops a group of sounds for the player.
     */
    fun stopSound(sound: SoundStop)

    /**
     * Stops all sounds of the given [category] for the player.
     */
    fun stopSound(category: SoundCategory)

    /**
     * Stops all sounds for the player.
     */
    fun stopAllSounds()

    /**
     * Play a note to the player at the given [location].
     *
     * If [location] is null, the players location will be used.
     */
    fun playNote(instrument: Instrument, note: Note, location: Location? = null)

    /**
     * Play a note to the player at the given [x], [y] and [z] coordinates.
     */
    fun playNote(instrument: Instrument, note: Note, x: Double, y: Double, z: Double)

}