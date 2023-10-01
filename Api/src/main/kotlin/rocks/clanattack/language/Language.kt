package rocks.clanattack.language

import net.kyori.adventure.text.ComponentLike

/**
 * A [Language] is used to store messages in the database.
 *
 * Messages are stored as key-value pairs,
 * where the key is always a [String] and
 * the value is a mini-message.
 */
@Suppress("unused")
interface Language {

    /**
     * The ISO 639-1 code of the language (e.g. `en` for English).
     */
    val isoCode: String

    /**
     * The name of the language.
     */
    val name: String

    /**
     * The skull value of the language.
     *
     * This skull is used to display the language in a GUI.
     */
    val skull: String

    /**
     * Whether the language is enabled.
     */
    val enabled: Boolean

    /**
     * Gets a message by its [key] and replaces placeholders with the given [replacement] block.
     */
    fun getMessage(key: String, replacement: Replacement.() -> Unit = {}): ComponentLike

}