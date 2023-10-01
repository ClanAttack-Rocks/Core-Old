package rocks.clanattack.util.log

/**
 * The [Logger] can be used to log messages.
 */
interface Logger {

    /**
     * Prints an informational message.
     *
     * If [printStackTrace] is true, a stack trace to the caller will be printed,
     * if no [throwable] is given.
     */
    fun info(message: String, throwable: Throwable? = null, printStackTrace: Boolean = false)

    /**
     * Prints a warning message.
     *
     * If [printStackTrace] is true, a stack trace to the caller will be printed,
     * if no [throwable] is given.
     */
    fun warn(message: String, throwable: Throwable? = null, printStackTrace: Boolean = false)

    /**
     * Prints an error message.
     *
     * If [printStackTrace] is true, a stack trace to the caller will be printed,
     * if no [throwable] is given.
     */
    fun error(message: String, throwable: Throwable? = null, printStackTrace: Boolean = false)

}