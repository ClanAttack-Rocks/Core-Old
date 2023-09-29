package rocks.clanattack.util.promise

/**
 * The result of a [Promise].
 */
interface PromiseResult<T> {

    /**
     * The state of the [Promise].
     */
    val state: PromiseState

    /**
     * The value of the [Promise] if it has been resolved.
     */
    val value: T?

    /**
     * The reason of the [Promise] if it has been rejected.
     */
    val reason: Throwable?

}