package rocks.clanattack.util.promise

/**
 * A [Promise] that can be fulfilled or rejected.
 *
 * The reason for splitting the [Promise] interface into [Promise] and [PromiseProvider] is that a [Promise] returned
 * from a service should not be able to be fulfilled or rejected by the user of the service. Only the service itself
 * should be able to fulfill or reject the [Promise].
 *
 * However, if a [Promise] is created by the user of the service, it should be able to be fulfilled or rejected by the
 * user.
 */
interface PromiseProvider<T : Any> : Promise<T> {

    /**
     * Fulfill the [Promise] with the given value.
     *
     * For effects of resolving a [Promise], see [Promise].
     *
     * @throws IllegalStateException If the [Promise] is already fulfilled or rejected.
     */
    fun fulfill(value: T)

    /**
     * Reject the [Promise] with the given reason.
     *
     * For effects of rejecting a [Promise], see [Promise].
     *
     * @throws IllegalStateException If the [Promise] is already fulfilled or rejected.
     */
    fun reject(reason: Throwable)

}