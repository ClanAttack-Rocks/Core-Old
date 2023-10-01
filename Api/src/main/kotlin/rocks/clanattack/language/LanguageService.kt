package rocks.clanattack.language

import rocks.clanattack.entry.service.Service

/**
 * The [LanguageService] is used to store languages in the database.
 */
@Suppress("unused")
interface LanguageService : Service {

    /**
     * All languages that are currently stored in the database.
     */
    val languages: List<Language>

    /**
     * All global placeholders.
     *
     * Global placeholders are composed of a key-value pair, where both are message keys.
     * The value of the placeholder will be replaced as [parsed][Replacement.parsed] in ever message.
     */
    val placeholders: Map<String, String>

    /**
     * Get a language by its [isoCode].
     */
    fun getLanguage(isoCode: String): Language

    /**
     * Adds a global placeholder.
     *
     * @see placeholders
     */
    fun addPlaceholder(key: String, value: String)

}