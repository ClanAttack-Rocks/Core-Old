package rocks.clanattack.language

import kotlinx.datetime.LocalDateTime
import net.kyori.adventure.text.ComponentLike

/**
 * The [Replacement] is used to replace placeholders in messages.
 *
 * @see Language.getMessage
 */
interface Replacement {

    /**
     * Add a component as a replacement.
     *
     * The [placeholder] will be replaced with the [component].
     */
    fun component(placeholder: String, component: ComponentLike)

    /**
     * Add an unparsed string as a replacement.
     *
     * The [placeholder] will be replaced with the literal [unparsed] string, ignoring any formatting.
     */
    fun unparsed(placeholder: String, unparsed: String)

    /**
     * Add a parsed string as a replacement.
     *
     * The [placeholder] will be replaced with the [parsed] string, respecting any formatting.
     */
    fun parsed(placeholder: String, parsed: String)

    /**
     * Add a number as a replacement.
     *
     * The [placeholder] will be replaced with the [number], respecting the number format of the placeholder.
     */
    fun number(placeholder: String, number: Number)

    /**
     * Add a date as a replacement.
     *
     * The [placeholder] will be replaced with the [date], respecting the date format of the placeholder.
     */
    fun date(placeholder: String, date: LocalDateTime)

    /**
     * Add a choice as a replacement.
     *
     * The [placeholder] will have different choices, where one will be chosen based on the [choice].
     */
    fun choice(placeholder: String, choice: Int)

    /**
     * Add a message as a replacement.
     *
     * The [placeholder] will be replaced with the [message] (getting it from the language file),
     * respecting the formatting of the gotten message.
     */
    fun message(placeholder: String, message: String)

}