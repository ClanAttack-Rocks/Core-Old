package rocks.clanattack.util.optional

/**
 * An optional value.
 *
 * @property value The value of the optional.
 */
class Optional<T : Any>(val value: T?) {

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