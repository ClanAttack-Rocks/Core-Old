package rocks.clanattack.util.log

/**
 * The [Logger] can be used to log messages.
 */
interface Logger {

    /**
     * Prints an informational message.
     */
    fun info(message: String, throwable: Throwable? = null)

    /**
     * Prints a warning message.
     */
    fun warn(message: String, throwable: Throwable? = null)

    /**
     * Prints an error message.
     */
    fun error(message: String, throwable: Throwable? = null)

}