package rocks.clanattack.util.promise

/**
 * A [Promise] that can be resolved or rejected.
 *
 * The reason for splitting the [Promise] interface into [Promise] and [PromiseProvider] is that a [Promise] returned
 * from a service should not be able to be resolved or rejected by the user of the service. Only the service itself
 * should be able to resolve or reject the [Promise].
 *
 * However, if a [Promise] is created by the user of the service, it should be able to resolve or reject the [Promise].
 */
interface PromiseProvider<T> : Promise<T> {

    /**
     * Resolve the [Promise] with the given value.
     *
     * For effects of resolving a [Promise], see [Promise].
     *
     * @throws IllegalStateException If the [Promise] is already resolved or rejected.
     */
    fun resolve(value: T)

    /**
     * Reject the [Promise] with the given reason.
     *
     * For effects of rejecting a [Promise], see [Promise].
     *
     * @throws IllegalStateException If the [Promise] is already resolved or rejected.
     */
    fun reject(reason: Throwable)

}