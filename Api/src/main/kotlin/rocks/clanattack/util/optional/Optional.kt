package rocks.clanattack.util.optional

/**
 * An optional value.
 *
 * @property value The value of the optional.
 */
data class Optional<T : Any>(val value: T?) {

    /**
     * Whether the optional is empty.
     */
    val isEmpty: Boolean
        get() = value == null

    /**
     * Whether the optional is present.
     */
    val isPresent: Boolean
        get() = value != null

    /**
     * Maps the [Optional] to a different type.
     */
    fun <R : Any> map(mapper: (T) -> R): Optional<R> = if (isEmpty) empty() else of(mapper(value!!))

    companion object {

        /**
         * Creates an [Optional] with the given value.
         */
        fun <T : Any> of(value: T): Optional<T> = Optional(value)

        /**
         * Creates an empty [Optional].
         */
        fun <T : Any> empty(): Optional<T> = Optional(null)

    }

}

/**
 * Creates an optional form the given value.
 */
fun <T : Any> T?.asOptional(): Optional<T> = Optional(this)