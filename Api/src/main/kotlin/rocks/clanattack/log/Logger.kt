package rocks.clanattack.log

/**
 * The [Logger] can be used to log messages.
 */
interface Logger {

    /**
     * Prints an informational message.
     */
    fun info(message: String)

    /**
     * Prints a warning message.
     */
    fun warn(message: String)

    /**
     * Prints an error message.
     */
    fun error(message: String, throwable: Throwable? = null)

}